package network.xyo.client.address

import org.spongycastle.asn1.sec.SECNamedCurves
import org.spongycastle.asn1.x9.X9ECParameters
import org.spongycastle.crypto.params.ECDomainParameters
import org.spongycastle.jce.provider.BouncyCastleProvider
import org.spongycastle.jce.spec.ECParameterSpec
import java.security.Security

abstract class EllipticKey(_size: Int): XyoData(_size) {
    companion object {
        val scInit = Security.insertProviderAt(BouncyCastleProvider(), 0)

        private val secp256k1Params: X9ECParameters = SECNamedCurves.getByName("secp256k1");
        val CURVE = ECDomainParameters(secp256k1Params.curve, secp256k1Params.g, secp256k1Params.n, secp256k1Params.h);
        val CURVE_SPEC = ECParameterSpec(secp256k1Params.curve, secp256k1Params.g, secp256k1Params.n, secp256k1Params.h);
    }
}