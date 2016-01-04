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

public class SupplierData implements Serializable
{
    private String name;
    private int status;
    private String city;

    public SupplierData(String name, int status, String city)
    {
        this.name = name;
        this.status = status;
        this.city = city;
    }

    public final String getName()
    {
        return name;
    }

    public final int getStatus()
    {
        return status;
    }

    public final String getCity()
    {
        return city;
    }

    public String toString()
    {
        return "[SupplierData: name=" + name +
               " status=" + status +
               " city=" + city + ']';
    }
}
