package com.cryptoadz.service;

import java.math.BigInteger;

import org.bouncycastle.crypto.digests.KeccakDigest;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.math.ec.custom.sec.SecP256K1Curve;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TronSigner {

    @Value("${tron.private-key}")
    private String privateKeyHex;

    private static final ECDomainParameters DOMAIN = new ECDomainParameters(
            new SecP256K1Curve(),
            new SecP256K1Curve().createPoint(
                    new BigInteger("79BE667EF9DCBBAC55A06295CE870B07029BFCDB2DCE28D959F2815B16F81798", 16),
                    new BigInteger("483ADA7726A3C4655DA4FBFC0E1108A8FD17B448A68554199C47D08FFB10D4B8", 16)
            ),
            new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141", 16)
    );

    public String sign(String rawDataHex) {
        BigInteger privKey = new BigInteger(privateKeyHex, 16);
        byte[] rawData = hexStringToByteArray(rawDataHex);

        byte[] hash = keccak256(rawData);
        ECDSASigner signer = new ECDSASigner();
        signer.init(true, new ECPrivateKeyParameters(privKey, DOMAIN));
        BigInteger[] signature = signer.generateSignature(hash);

        BigInteger r = signature[0];
        BigInteger s = signature[1];
        s = s.compareTo(DOMAIN.getN().shiftRight(1)) > 0 ? DOMAIN.getN().subtract(s) : s;

        byte[] rBytes = toFixedLength(r.toByteArray(), 32);
        byte[] sBytes = toFixedLength(s.toByteArray(), 32);

        return byteArrayToHex(rBytes) + byteArrayToHex(sBytes);
    }

    private static byte[] keccak256(byte[] input) {
        KeccakDigest keccak = new KeccakDigest(256);
        keccak.update(input, 0, input.length);
        byte[] hash = new byte[32];
        keccak.doFinal(hash, 0);
        return hash;
    }

    private static byte[] toFixedLength(byte[] input, int length) {
        if (input.length == length) return input;
        byte[] result = new byte[length];
        System.arraycopy(input, Math.max(0, input.length - length), result, length - Math.min(length, input.length), Math.min(length, input.length));
        return result;
    }

    private static String byteArrayToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2)
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        return data;
    }
}

