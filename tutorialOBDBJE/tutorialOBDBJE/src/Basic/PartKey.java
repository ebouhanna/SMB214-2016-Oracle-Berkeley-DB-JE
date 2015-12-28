/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Basic;

/**
 *
 * @author lenovo
 */
import java.io.Serializable;

public class PartKey implements Serializable
{
    private final String number;

    public PartKey(String number) {
        this.number = number;
    }

    public final String getNumber() {
        return number;
    }

    @Override
    public String toString() {
        return "[PartKey: number=" + number + ']';
    }
} 
