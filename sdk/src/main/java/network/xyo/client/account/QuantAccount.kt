package network.xyo.client.account

import network.xyo.client.account.model.Account
import network.xyo.client.account.model.AccountStatic
import network.xyo.client.account.model.PreviousHashStore
import network.xyo.client.lib.hexStringToByteArray
import org.bouncycastle.jcajce.provider.digest.Keccak
import org.bouncycastle.crypto.params.MLDSAKeyGenerationParameters
import org.bouncycastle.crypto.generators.MLDSAKeyPairGenerator
import org.bouncycastle.crypto.params.MLDSAParameters
import org.bouncycastle.crypto.params.MLDSAPrivateKeyParameters
import org.bouncycastle.crypto.params.MLDSAPublicKeyParameters
import org.bouncycastle.crypto.signers.MLDSASigner
import java.security.SecureRandom
import org.bouncycastle.crypto.params.ParametersWithRandom

open class QuantAccount private constructor (
    private val _seed: ByteArray,
    private val _privateKeyParams: MLDSAPrivateKeyParameters,
    private val _publicKeyParams: MLDSAPublicKeyParameters,
    private var _previousHash: ByteArray? = null
): Account {

    private constructor(seed: ByteArray, keyPair: Pair<MLDSAPrivateKeyParameters, MLDSAPublicKeyParameters>, previousHash: ByteArray?) : this(
        seed,
        keyPair.first,
        keyPair.second,
        previousHash
    )

    constructor(seed: ByteArray, previousHash: ByteArray? = null) : this(
        seed,
        generateKeyPair(seed),
        previousHash
    )

    private val _address = addressFromPublicKey(_publicKeyParams.encoded)

    final override val address: ByteArray
        get() = _address
    
    final override val addressString: String
        get() = network.xyo.client.account.address.XyoAddress.encodeQuantAddress("qm65", _address)
    
    final override val previousHash: ByteArray?
        get() = _previousHash
        
    final override val privateKey: ByteArray
        get() = _seed // The TS implementation maps privateKey to the 32-byte seed
        
    final override val publicKey: ByteArray
        get() = _publicKeyParams.encoded
        
    final override val publicKeyUncompressed: ByteArray
        get() = _publicKeyParams.encoded

    override suspend fun sign(hash: ByteArray): ByteArray {
        val signer = MLDSASigner()
        signer.init(true, ParametersWithRandom(_privateKeyParams, SecureRandom()))
        signer.update(hash, 0, hash.size)
        val signature = signer.generateSignature()
        _previousHash = hash
        previousHashStore?.setItem(address, hash)
        return signature
    }

    override fun verify(msg: ByteArray, signature: ByteArray): Boolean {
        val signer = MLDSASigner()
        signer.init(false, _publicKeyParams)
        signer.update(msg, 0, msg.size)
        return signer.verifySignature(signature)
    }

    companion object: AccountStatic<Account> {
        override var previousHashStore: PreviousHashStore? = null

        override fun fromPrivateKey(key: ByteArray): Account {
            return QuantAccount(key)
        }

        override fun fromPrivateKey(key: String): Account {
            return fromPrivateKey(hexStringToByteArray(key))
        }

        override fun random(): Account {
            val random = SecureRandom()
            val seed = ByteArray(32)
            random.nextBytes(seed)
            return fromPrivateKey(seed)
        }

        private fun addressFromPublicKey(publicKey: ByteArray): ByteArray {
            val keccak = Keccak.Digest256()
            keccak.update(publicKey)
            val hash = keccak.digest()
            // Assume 20 byte address (like Ethereum/Account) from the end of the hash
            return hash.copyOfRange(12, hash.size)
        }

        private fun generateKeyPair(seed: ByteArray): Pair<MLDSAPrivateKeyParameters, MLDSAPublicKeyParameters> {
            val generator = MLDSAKeyPairGenerator()
            // Provide a determinisic SecureRandom that simply outputs the seed once
            val deterministicRandom = object : SecureRandom() {
                private var bytesProvided = 0
                override fun nextBytes(bytes: ByteArray) {
                    val toCopy = minOf(bytes.size, seed.size - bytesProvided)
                    if (toCopy > 0) {
                        System.arraycopy(seed, bytesProvided, bytes, 0, toCopy)
                        bytesProvided += toCopy
                    }
                }
            }
            val keyGenParams = MLDSAKeyGenerationParameters(deterministicRandom, MLDSAParameters.ml_dsa_65)
            generator.init(keyGenParams)
            val keyPair = generator.generateKeyPair()
            return Pair(keyPair.private as MLDSAPrivateKeyParameters, keyPair.public as MLDSAPublicKeyParameters)
        }
    }
}
