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

public class Poids implements Serializable
{
    public final static String GRAMMES = "grammes";
    public final static String ONCES = "onces";

    private double nombre;
    private String unites;

    public Poids(double nombre, String unites)
    {
        this.nombre = nombre;
        this.unites = unites;
    }

    public final double getNombre()
    {
        return nombre;
    }

    public final String getUnites()
    {
        return unites;
    }

    public String toString()
    {
        return "[" + nombre + ' ' + unites + ']';
    }
} 