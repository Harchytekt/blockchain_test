package coin

class TransactionInput {
    String transactionOutputId
    TransactionOutput UTXO

    TransactionInput(String transactionOutputId) {
        this.transactionOutputId = transactionOutputId
    }
}
