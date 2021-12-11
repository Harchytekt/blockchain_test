package coin

import utils.StringUtils

import java.security.PublicKey

class TransactionOutput {
    String id
    PublicKey recipient
    float value
    String parentTransactionId

    TransactionOutput(PublicKey recipient, float value, String parentTransactionId) {
        this.recipient = recipient
        this.value = value
        this.parentTransactionId = parentTransactionId
        this.id = "${StringUtils.getStringFromKey(recipient)}$value$parentTransactionId".digest('SHA-256')
    }

    boolean isMine(PublicKey publicKey) {
        return publicKey == recipient
    }
}
