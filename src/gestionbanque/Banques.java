/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gestionbanque;

import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author firas
 */
public class Banques {
    
    private static ArrayList<Banque> allBanque;
    
    public Banques()
    {
        this.allBanque= new ArrayList<Banque>();
    }
    
    //controle de saisie fel main()
    public void addBanque(Banque bnq)
    {
        this.getAllBanque().add(bnq);
    }
    
    public boolean removeBanque(String codeBanque)
    {
        int j=0;
        boolean existe=false;
        for(int i=0;i<this.getAllBanque().size();i++)
        {
            if(this.getAllBanque().get(i).getCodeBanque().compareTo(codeBanque)==0)
            {
                j=i;
                existe=true;
                break;
            }
        }
        
        if(existe)
        {
            this.getAllBanque().remove(j);
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * @return the allBanque
     */
    public static ArrayList<Banque> getAllBanque() {
        return allBanque;
    }

    /**
     * @param allBanque the allBanque to set
     */
    public void setAllBanque(ArrayList<Banque> allBanque) {
        this.allBanque = allBanque;
    }
    
    public static String ibanCodeBanque(String iban)
    {
        return iban.substring(0, 7);
    }
    
    public Banque creerBanque()
    {
        Scanner sc = new Scanner(System.in);
        String nom,code;
        int tt;
        boolean test=false,existe=false;
        System.out.println("Entrer le nom de banque");
        nom= sc.next();
        
        do
        {
            System.out.println("Entrer le code du banque par exemple 'TN11102'");
            code=sc.next();
            if(code.length()==7)
            {
                try
                {
                    tt=Integer.parseInt(code.substring(2, code.length()));
                    test=true;
                }catch(Exception e)
                {
                    System.out.println("Le code que vous avez entrée est non acceptable");
                    //this.creerBanque();
                }
            }
            else
            {
                System.out.println("le langueur de code faut etre egale à 7 caractére");
            }
        }while(test==false);
        
        for(int i=0;i<this.getAllBanque().size();i++)
        {
            if(this.getAllBanque().get(i).getCodeBanque().compareTo(code)==0)
            {
                existe=true;
                break;
            }
        }
        if(existe)
        {
            System.out.println("Cette code bancaire est déja affecter à une autre banque");
            return null;
        }
        else
        {
            Banque bn= new Banque(nom, code);
            this.addBanque(bn);
            return bn;
        }
        
        
    }
    
    
    
}
