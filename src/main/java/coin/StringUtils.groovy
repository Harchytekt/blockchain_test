package coin

import java.security.Key
import java.security.PrivateKey
import java.security.PublicKey
import java.security.Signature

class StringUtils {
    // Applies ECDSA Signature and returns the result (as bytes)
    static byte[] applyECDSASig(PrivateKey privateKey, String input) {
        Signature dsa
        byte[] output

        try {
            dsa = Signature.getInstance("ECDSA", "BC")
            dsa.initSign(privateKey)

            byte[] strByte = input.getBytes()
            dsa.update(strByte)

            byte[] realSig = dsa.sign()
            output = realSig
        } catch (Exception e) {
            throw new RuntimeException(e)
        }
        return output
    }

    // Verifies a String signature
    static boolean verifyECDSASig(PublicKey publicKey, String data, byte[] signature) {
        try {
            Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC")
            ecdsaVerify.initVerify(publicKey)
            ecdsaVerify.update(data.getBytes())
            return ecdsaVerify.verify(signature)
        } catch (Exception e) {
            throw new RuntimeException(e)
        }
    }

    static String getStringFromKey(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded())
    }

    // Tracks in array of transactions and returns a merkle root (more or less)
    static String getMerkleRoot(List<Transaction> transactions) {
        int count = transactions.size()
        List<String> previousTreeLayer = new ArrayList<>()
        transactions.each { transaction ->
            previousTreeLayer.add(transaction.transactionId)
        }

        List<String> treeLayer = previousTreeLayer
        while (count > 1) {
            treeLayer = new ArrayList<>()
            for (i in 1..<previousTreeLayer.size()) {
                treeLayer.add("${previousTreeLayer.get(i - 1)}${previousTreeLayer.get(i)}".digest('SHA-256'))
            }
            count = treeLayer.size()
            previousTreeLayer = treeLayer
        }

        return treeLayer.size() == 1 ? treeLayer.get(0) : ""
    }

    static String getDifficultyString(int difficulty) {
        return new String(new char[difficulty]).replace('\0', '0')
    }
}
