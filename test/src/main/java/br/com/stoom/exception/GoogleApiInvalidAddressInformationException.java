package br.com.stoom.exception;

public class GoogleApiInvalidAddressInformationException extends IllegalArgumentException {
    public GoogleApiInvalidAddressInformationException() {
        super("Invalid address information to retrieve latitude and longitude from google");
    }
}
