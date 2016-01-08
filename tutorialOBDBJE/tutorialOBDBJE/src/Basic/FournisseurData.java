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

public class FournisseurData implements Serializable
{
    private String nom;
    private int status;
    private String ville;

    public FournisseurData(String nom, int status, String ville)
    {
        this.nom = nom;
        this.status = status;
        this.ville = ville;
    }

    public final String getNom()
    {
        return nom;
    }

    public final int getStatus()
    {
        return status;
    }

    public final String getVille()
    {
        return ville;
    }

    public String toString()
    {
        return "[FournisseurData: nom=" + nom +
               " status=" + status +
               " ville=" + ville + ']';
    }
}
