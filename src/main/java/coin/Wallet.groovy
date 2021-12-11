package coin

import java.security.*
import java.security.spec.ECGenParameterSpec

class Wallet {
    PrivateKey privateKey
    PublicKey publicKey

    HashMap<String, TransactionOutput> UTXOs = new HashMap<>()

    Wallet() {
        generateKeyPair()
    }

    void generateKeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("ECDSA", "BC")
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG")
            ECGenParameterSpec ecGenParameterSpec = new ECGenParameterSpec("prime192v1")
            keyPairGenerator.initialize(ecGenParameterSpec, secureRandom)
            KeyPair keyPair = keyPairGenerator.generateKeyPair()
            privateKey = keyPair.getPrivate()
            publicKey = keyPair.getPublic()
        } catch (Exception e) {
            throw new RuntimeException(e)
        }
    }

    // Returns balance and stores the UTXOs owned by this wallet in this.UTXOs
    float getBalance() {
        float total = 0
        CoinTest.UTXOs.entrySet().each {
            TransactionOutput UTXO = it.value
            if (UTXO.isMine(publicKey)) {
                UTXOs.put(UTXO.id, UTXO)
                total += UTXO.value
            }
        }
        return total
    }

    // Generates and return a new transaction from this wallet
    Transaction sendFunds(PublicKey recipient, float value) {
        if (getBalance() < value) {
            println "Not enough funds to send the transaction. Transaction discarded."
            return null
        }

        List<TransactionInput> inputs = new ArrayList<>()

        float total = 0
        for (Map.Entry<String, TransactionOutput> it in UTXOs.entrySet()) {
            TransactionOutput UTXO = it.value
            total += UTXO.value
            inputs.add(new TransactionInput(UTXO.id))
            if (total > value) {
                break
            }
        }

        Transaction newTransaction = new Transaction(publicKey, recipient, value, inputs)
        newTransaction.generateSignature(privateKey)

        inputs.each { input ->
            UTXOs.remove(input.transactionOutputId)
        }

        return newTransaction
    }
}
