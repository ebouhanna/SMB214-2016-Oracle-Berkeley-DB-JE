/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Basic;

/**
 *
 * @author ebouhanna
 */
import java.io.Serializable;

public class ShipmentData implements Serializable
{
    private int quantity;

    public ShipmentData(int quantity)
    {
        this.quantity = quantity;
    }

    public final int getQuantity()
    {
        return quantity;
    }

    public String toString()
    {
        return "[ShipmentData: quantity=" + quantity + ']';
    }
} 
