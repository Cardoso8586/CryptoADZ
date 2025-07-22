package com.cryptoadz.service;

import java.math.BigInteger;
import java.util.Arrays;

public class TronAddressUtils {

    private static final String BASE58_ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";

    public static byte[] base58CheckDecode(String base58) throws IllegalArgumentException {
        BigInteger num = BigInteger.ZERO;
        for (char ch : base58.toCharArray()) {
            int digit = BASE58_ALPHABET.indexOf(ch);
            if (digit < 0) throw new IllegalArgumentException("Invalid Base58 character: " + ch);
            num = num.multiply(BigInteger.valueOf(58)).add(BigInteger.valueOf(digit));
        }
        byte[] fullBytes = num.toByteArray();

        if (fullBytes.length > 1 && fullBytes[0] == 0) {
            fullBytes = Arrays.copyOfRange(fullBytes, 1, fullBytes.length);
        }

        if (fullBytes.length < 5) {
            throw new IllegalArgumentException("Invalid Base58Check data: too short");
        }
        return Arrays.copyOfRange(fullBytes, 0, fullBytes.length - 4); // remove checksum
    }
}
