package Exceptions;

public class DuplicateProductException extends RuntimeException{
    public DuplicateProductException(String message){
        super(message);
    }
}
