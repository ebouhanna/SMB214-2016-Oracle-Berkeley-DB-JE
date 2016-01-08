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

public class ProduitData implements Serializable
{
    private String nom;
    private String couleur;
    private Poids poids;
    private String ville;

    /**
     * Java doc:
     * @param nom
     * @param couleur
     * @param poids
     * @param ville
     */
    public ProduitData(String nom, String couleur, Poids poids, String ville)
    {
        this.nom = nom;
        this.couleur = couleur;
        this.poids = poids;
        this.ville = ville;
    }

    public final String getNom()
    {
        return nom;
    }

    public final String getCouleur()
    {
        return couleur;
    }

    public final Poids getPoids()
    {
        return poids;
    }

    public final String getVille()
    {
        return ville;
    }

    public String toString()
    {
        return "[ProduitData: nom=" + nom +
               " couleur=" + couleur +
               " poids=" + poids +
               " ville=" + ville + ']';
    }
} 
