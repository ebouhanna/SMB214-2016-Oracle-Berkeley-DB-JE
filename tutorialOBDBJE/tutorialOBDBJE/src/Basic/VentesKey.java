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

public class VentesKey implements Serializable
{
    private String produitNombre;
    private String fournisseurNombre;

    public VentesKey(String partNumber, String fournisseurNombre)
    {
        this.produitNombre = partNumber;
        this.fournisseurNombre = fournisseurNombre;
    }

    public final String getProduitNombre()
    {
        return produitNombre;
    }

    public final String getFournisseurNombre()
    {
        return fournisseurNombre;
    }

    public String toString()
    {
        return "[VentesKey: fournisseur=" + fournisseurNombre +
                " produit=" + produitNombre + ']';
    }
} 
//