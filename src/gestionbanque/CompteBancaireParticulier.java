/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gestionbanque;

import java.util.Scanner;

/**
 *
 * @author firas
 */
public class CompteBancaireParticulier extends Compte_bancaire {
    private double debitMax;
    private String numeroCarteBancaire;
    
    public CompteBancaireParticulier(String iban,String type,String etat,Titulaire_compte titulaire,double solde,double decouvertAutoriser,double debitMax,String numeroCarteBancaire,String dateCreation,String dateMAJ,String dateDernierVersement)
    {
        super(iban,type,etat,titulaire,solde,decouvertAutoriser,dateCreation,dateMAJ,dateDernierVersement);
        this.debitMax=debitMax;
        this.numeroCarteBancaire=numeroCarteBancaire;
                
    }
    
    public CompteBancaireParticulier()
    {
        
    }
    
    public void afficher()
    {
        super.afficher();
        System.out.println(", Debit max: "+this.debitMax+", Numero carte bancaire: "+this.numeroCarteBancaire);
    }

    /**
     * @return the debitMax
     */
    public double getDebitMax() {
        return debitMax;
    }

    /**
     * @param debitMax the debitMax to set
     */
    public void setDebitMax(double debitMax) {
        this.debitMax = debitMax;
    }

    /**
     * @return the numeroCarteBancaire
     */
    public String getNumeroCarteBancaire() {
        return numeroCarteBancaire;
    }

    /**
     * @param numeroCarteBancaire the numeroCarteBancaire to set
     */
    public void setNumeroCarteBancaire(String numeroCarteBancaire) {
        this.numeroCarteBancaire = numeroCarteBancaire;
    }
    
    //creation compte bancaire
    public void creerCompte(String code,Titulaire_compte titulaire)
    {
        Scanner sc = new Scanner(System.in);
        super.creerCompte(code, titulaire);
        System.out.println("entrer le debit max de ce compte");
        this.debitMax=sc.nextDouble();
        
        
        boolean test=false;
        int tt,type;
        String num;
        do
        {
            System.out.println("entrer le numero de carte bancaire");
            num = sc.next();
            try
            {
                for(int i=0;i<num.length();i+=4)
                {
                    if(i+4<=num.length()-1)
                    {
                        tt=Integer.parseInt(num.substring(i, i+4));
                    }
                    else
                    {
                        tt=Integer.parseInt(num.substring(i, num.length()-1));
                    }
                    
                }
                test=true;
                
            }catch(NumberFormatException e)
            {
                System.out.println("il faut ecrire des chiffres");
                test=false;
            }
            
        }while((test==false)||(num.length()!=16));
        
        this.numeroCarteBancaire=num;
        
        
    }
    
    
    
}
