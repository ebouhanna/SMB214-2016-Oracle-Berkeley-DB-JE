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

public class Weight implements Serializable
{
    public final static String GRAMS = "grams";
    public final static String OUNCES = "ounces";

    private double amount;
    private String units;

    public Weight(double amount, String units)
    {
        this.amount = amount;
        this.units = units;
    }

    public final double getAmount()
    {
        return amount;
    }

    public final String getUnits()
    {
        return units;
    }

    public String toString()
    {
        return "[" + amount + ' ' + units + ']';
    }
} 
