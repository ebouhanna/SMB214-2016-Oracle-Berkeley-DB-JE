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

public class VentesData implements Serializable
{
    private int quantite;

    public VentesData(int quantite)
    {
        this.quantite = quantite;
    }

    public final int getQuantite()
    {
        return quantite;
    }

    public String toString()
    {
        return "[VentesData: quantite=" + quantite + ']';
    }
} 
