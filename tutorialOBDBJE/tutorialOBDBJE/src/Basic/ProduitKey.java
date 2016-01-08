/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Basic;

/**
 *
 * @author ebouhanna
 * Class oour definir l'Objet clé de chaque produit qui sera insérer dans la BD 
 */

import java.io.Serializable;

public class ProduitKey implements Serializable
{
    private final String nombre;

    public ProduitKey(String nombre) {
        this.nombre = nombre;
    }

    public final String getNombre() {
        return nombre;
    }

    @Override
    public String toString() {
        return "[ProduitKey: nombre=" + nombre + ']';
    }
} 
