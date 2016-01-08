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

public class FournisseurKey implements Serializable
{
    private String nombre;

    public FournisseurKey(String nombre)
    {
        this.nombre = nombre;
    }

    public final String getNombre()
    {
        return nombre;
    }

    public String toString()
    {
        return "[FournisseurKey: nombre=" + nombre + ']';
    }
} 
