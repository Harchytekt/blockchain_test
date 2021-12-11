package coin

import utils.StringUtils

import java.security.PrivateKey
import java.security.PublicKey

class Transaction {
    String transactionId
    PublicKey sender
    PublicKey recipient
    float value
    byte[] signature

    List<TransactionInput> inputs = new ArrayList<>()
    List<TransactionOutput> outputs = new ArrayList<>()

    static int sequence = 0

    Transaction(PublicKey sender, PublicKey recipient, float value, List<TransactionInput> inputs) {
        this.sender = sender
        this.recipient = recipient
        this.value = value
        this.inputs = inputs
    }

    private String calculateHash() {
        sequence++
        return "$sender$recipient$value$sequence".digest('SHA-256')
    }

    // Signs all the data we don't wish to be tempered with
    void generateSignature(PrivateKey privateKey) {
        String data = StringUtils.getStringFromKey(sender) + StringUtils.getStringFromKey(recipient) + Float.toString(value)
        signature = StringUtils.applyECDSASig(privateKey, data)
    }

    // Verifies the data we signed hasn't been tempered with
    boolean verifySignature() {
        String data = StringUtils.getStringFromKey(sender) + StringUtils.getStringFromKey(recipient) + Float.toString(value)
        return StringUtils.verifyECDSASig(sender, data, signature)
    }

    // True if a new transaction is created
    boolean processTransaction() {
        if (!verifySignature()) {
            println "Transaction Signature failed to verify"
            return false
        }

        // Gather transactions inputs
        inputs.each { input ->
            input.UTXO = CoinTest.UTXOs.get(input.transactionOutputId)
        }

        // Check if transaction is valid
        if (getInputsValue() < CoinTest.minimumTransaction) {
            println "Transaction Inputs too small: ${getInputsValue()}"
            return false
        }

        // Generate transaction outputs
        float leftOver = (getInputsValue() - value) as float
        transactionId = calculateHash()
        outputs.add(new TransactionOutput(this.recipient, value, transactionId))
        outputs.add(new TransactionOutput(this.sender, leftOver, transactionId))

        // Add outputs to Unspent list
        outputs.each { output ->
            CoinTest.UTXOs.put(output.id, output)
        }

        // Remove transaction inputs from UTXO lists as spent
        for (TransactionInput input in inputs) {
            if (input.UTXO == null) {
                continue
            }
            CoinTest.UTXOs.remove(input.UTXO.id)
        }

        return true
    }

    // Returns sum of inputs(UTXOs) values
    float getInputsValue() {
        float total = 0
        for (TransactionInput input in inputs) {
            if (input.UTXO == null) {
                continue
            }
            total += input.UTXO.value
        }
        return total
    }

    // Returns sum of outputs
    float getOutputsValue() {
        float total = 0
        outputs.each { output ->
            total += output.value
        }
        return total
    }
}