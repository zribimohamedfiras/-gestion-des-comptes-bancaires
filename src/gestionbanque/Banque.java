/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gestionbanque;

import com.mongodb.BasicDBObject;
import com.mongodb.Cursor;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoClientURI;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author firas
 */
public class Banque implements OrganismeBancaire{
    
    private String nomBanque;
    private String codeBanque;
    private ArrayList<Compte_bancaire> comptes;
    private ArrayList<Personne_physique> titulairesP;
    private ArrayList<Personne_moral> titulairesM;
    
    
    public Banque(String nomBanque,String codeBanque)
    {
        this.nomBanque=nomBanque;
        this.codeBanque=codeBanque;
        this.comptes= new ArrayList<Compte_bancaire>();
        this.titulairesP = new ArrayList<Personne_physique>();
        this.titulairesM = new ArrayList<Personne_moral>();
    }

    /**
     * @return the nomBanque
     */
    public String getNomBanque() {
        return nomBanque;
    }

    /**
     * @param nomBanque the nomBanque to set
     */
    public void setNomBanque(String nomBanque) {
        this.nomBanque = nomBanque;
    }

    /**
     * @return the codeBaque
     */
    public String getCodeBanque() {
        return codeBanque;
    }

    /**
     * @param codeBaque the codeBaque to set
     */
    public void setCodeBanque(String codeBaque) {
        this.codeBanque = codeBaque;
    }

    /**
     * @return the comptes
     */
    public ArrayList<Compte_bancaire> getComptes() {
        return comptes;
    }

    /**
     * @param comptes the comptes to set
     */
    public void setComptes(ArrayList<Compte_bancaire> comptes) {
        this.comptes = comptes;
    }

    public ArrayList<Personne_physique> getTitulairesP() {
        return titulairesP;
    }

    public ArrayList<Personne_moral> getTitulairesM() {
        return titulairesM;
    }

    public void setTitulairesP(ArrayList<Personne_physique> titulairesP) {
        this.titulairesP = titulairesP;
    }

    public void setTitulairesM(ArrayList<Personne_moral> titulairesM) {
        this.titulairesM = titulairesM;
    }
    
    public void addTitulaireP(Personne_physique titulaire)
    {
        this.titulairesP.add(titulaire);
    }
    public boolean removeTitulaireP(Personne_physique titulaire)
    {
        if(this.titulairesP.indexOf(titulaire)!=-1)
        {
            this.titulairesP.remove(titulaire);
            return true;
        }
        else
        {
            System.out.println("titulaire inexistant");
            return false;
        }
    }
    
    public void addTitulaireM(Personne_moral titulaire)
    {
        this.titulairesM.add(titulaire);
    }
    public boolean removeTitulaireM(Personne_moral titulaire)
    {
        if(this.titulairesM.indexOf(titulaire)!=-1)
        {
            this.titulairesM.remove(titulaire);
            return true;
        }
        else
        {
            System.out.println("titulaire inexistant");
            return false;
        }
    }
    
    public void addCompte(Compte_bancaire compte)
    {
        if(this.comptes.contains(compte))
        {
            System.out.println("compte bancaire deja existant");
        }
        else
        {
            this.comptes.add(compte);
            System.out.println("Ajout effectuer avec succée");
        }
    }
    
    // sta3malet return boolean bech fel tafsi5 ken i7eb fel main i3awed ya3mel saisie ll iban marra o5ra !
    public boolean removeCompte(String iban)
    {
        int j=0;
        boolean existe=false;
        for(int i=0;i<this.comptes.size();i++)
        {
            if(this.comptes.get(i).getIban().compareTo(iban)==0)
            {
                j=i;
                existe=true;
                break;
            }
        }
        
        if(existe)
        {
            this.comptes.remove(j);
            return true;
        }
        else
        {
            return false;
        }
        
    }
    

    
    public boolean debiterCompte(String iban, double montant,DBCollection coll_compte_particulier,DBCollection coll_compte_entreprise) throws CompteBancaireInexistant,DecouvertAutorise,DepasseDebitMax,EtatException
    {
        int j=0;
        boolean existe=false,debite=true,etat=true;
        
        for(int i=0;i<this.comptes.size();i++)
        {
            if(this.comptes.get(i).getIban().compareTo(iban)==0)
            {
                j=i;
                existe=true;
                break;
            }
        }
        
        if(existe)
        {
            if(this.comptes.get(j) instanceof CompteBancaireParticulier)
            {
                
                CompteBancaireParticulier cp=(CompteBancaireParticulier) this.comptes.get(j);
                
                if(cp.getDebitMax()<montant)
                {
                    debite=false;
                }
            }
            
            if((this.comptes.get(j).getEtat().compareTo("bloquer")==0)||(this.comptes.get(j).getEtat().compareTo("fermer")==0))
            {
                etat=false;
                
            }
            
            if((debite==true)&&(etat==true))
            {
                
                if((this.comptes.get(j).getSolde()+this.comptes.get(j).getDecouvertAutoriser())>=montant)
                {
                    this.comptes.get(j).setSolde(this.comptes.get(j).getSolde()-montant);
                    
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                    String late;
        
                    LocalDate today = LocalDate.now();
                    String day,now ;
        
                    late = LocalTime.now().format(formatter);
                    day =  today.toString();
                    now  = day+ " "+late;
                    this.comptes.get(j).setDateMAJ(now);
                    
                    
                    // liaison mongoDB modification 
                    
                    //update document
                    BasicDBObject updateDocument = new BasicDBObject();
                    //new value 
                    updateDocument.append("$set", new BasicDBObject()
                    .append("solde",this.comptes.get(j).getSolde())
                    .append("dateMAJ", now)
                    );
                    //old document
                    BasicDBObject oldDocument = new BasicDBObject().append("iban", iban);
                    
                    if(this.comptes.get(j) instanceof CompteBancaireParticulier)
                    {
                        coll_compte_particulier.update(oldDocument, updateDocument,false,true);
                    }
                    else
                    {
                        coll_compte_entreprise.update(oldDocument, updateDocument,false,true);
                    }
                    System.out.println("operation effectuer avec succée");
                    this.comptes.get(j).afficher();
                    return true;
                }
                else
                {
                    //decouvertException
                    throw new DecouvertAutorise("decouvert ne vous permet pas de faire cette operation");
                    //System.out.println("solde insuffisant");
                    //return false;
                }
            }
            else
            {
                if(!debite)
                {
                    //depasseDebitmaxException
                    throw new DepasseDebitMax("votre compte ne vous permet pas de debiter cette quantité d'argent");
                    //System.out.println("votre compte ne vous permet pas de debiter cet quantité d'argent");
                }
                if(!etat)
                {
                    String msg="vous ne pouvez pas debiter d'un compte "+this.comptes.get(j).getEtat();
                    //etatException
                    throw new EtatException(msg);
                    //System.out.println("vous ne pouvez pas debiter un compte "+this.comptes.get(j).getEtat());
                }
                
                return false;
            }
            
        }
        else
        {
            //compteInexisteException
            throw new CompteBancaireInexistant("compte bancaire inexistant");
            //System.out.println("compte bancaire introuvable");
            //return false;
        }
        
    }
    
    public boolean crediterCompte(String iban, double montant,DBCollection coll_compte_particulier,DBCollection coll_compte_entreprise) throws CompteBancaireInexistant
    {
        int j=0;
        boolean existe=false;
        for(int i=0;i<this.comptes.size();i++)
        {
            if(this.comptes.get(i).getIban().compareTo(iban)==0)
            {
                j=i;
                existe=true;
                break;
            }
        }
        
        if(existe)
        {
            if(montant>0)
            {
                this.comptes.get(j).setSolde(this.comptes.get(j).getSolde()+montant);
                
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                String late;
        
                LocalDate today = LocalDate.now();
                String day,now ;
    
                late = LocalTime.now().format(formatter);
                day =  today.toString();
                now  = day+ " "+late;
                this.comptes.get(j).setDateMAJ(now);
                this.comptes.get(j).setDateDernierVersement(now);
                
                //update document
                    BasicDBObject updateDocument = new BasicDBObject();
                    //new value 
                    updateDocument.append("$set", new BasicDBObject()
                    .append("solde",this.comptes.get(j).getSolde())
                    .append("dateMAJ", now)
                    .append("dateDernierVersement", now)
                    );
                    //old document
                    BasicDBObject oldDocument = new BasicDBObject().append("iban", iban);
                    
                    if(this.comptes.get(j) instanceof CompteBancaireParticulier)
                    {
                        coll_compte_particulier.update(oldDocument, updateDocument,false,true);
                    }
                    else
                    {
                        coll_compte_entreprise.update(oldDocument, updateDocument,false,true);
                    }
                
                System.out.println("operation effectuée avec succée");
                this.comptes.get(j).afficher();
                return true;
            }
            else
            {
                System.out.println("montant inferieur à zero");
                return false;
            }
        }
        else
        {
            throw new CompteBancaireInexistant("compte bancaire inexistant");
            //System.out.println("compte bancaire introuvable");
            //return false;
        }
    }
    
    //exception iban source ou iban destiantion incorect 
    //exception montant insuffisable 
    public boolean virerArgent(String iban_source,String iban_destination, double montant,DBCollection coll_compte_particulier,DBCollection coll_compte_entreprise) throws CompteBancaireInexistant,DecouvertAutorise,DepasseDebitMax,EtatException,EpargneVirementException
    {
        ArrayList<Banque> banques ;
        String banqueiban=Banques.ibanCodeBanque(iban_destination);
        int j=0,k=0,l=0;
        boolean existe=false,debite=true,etat=true,couran=true;
        for(int i=0;i<this.comptes.size();i++)
        {
            if(this.comptes.get(i).getIban().compareTo(iban_source)==0)
            {
                j=i;
                existe=true;
                break;
            }
        }
        boolean testBanque=false,testCompte=false;
        
        if(existe)
        {
            if(this.comptes.get(j) instanceof CompteBancaireParticulier)
            {
                CompteBancaireParticulier cp=(CompteBancaireParticulier) this.comptes.get(j);
                if(cp.getDebitMax()<montant)
                {
                    debite=false;
                }
            }
            if((this.comptes.get(j).getEtat().compareTo("bloquer")==0)||(this.comptes.get(j).getEtat().compareTo("fermer")==0))
            {
                etat=false;
                
            }
            
            if(this.comptes.get(j).getType().compareTo("epargne")==0)
            {
                couran=false;
            }
        
            if(debite && etat && couran)
            {
                if((this.comptes.get(j).getSolde()+this.comptes.get(j).getDecouvertAutoriser())>=montant)
                {
                    banques = Banques.getAllBanque();
                    for(int i=0;i<banques.size();i++)
                    {
                        if(banques.get(i).getCodeBanque().compareTo(banqueiban)==0)
                        {
                            testBanque=true;
                            k=i;
                            for(int m=0;m<banques.get(k).comptes.size();m++)
                            {
                                if(banques.get(k).getComptes().get(m).getIban().compareTo(iban_destination)==0)
                                {
                                    testCompte=true;
                                    l=m;
                                    if(banques.get(k).getComptes().get(m).getType().compareTo("epargne")==0)
                                    {
                                        throw new EpargneVirementException("vous ne pouvez pas effectuer un virement vers un compte epargne");
                                        //System.out.println("vous ne pouvez pas effectuer une virement vers un compte epargne");
                                        //return false;
                                    }
                                    else
                                    {
                                        banques.get(k).crediterCompte(iban_destination, montant,coll_compte_particulier,coll_compte_entreprise);
                                        this.debiterCompte(iban_source, montant,coll_compte_particulier,coll_compte_entreprise);
                                        
                                        //mise à jours dans la base de donner de nouveau compte
                                    }
                                    
                                    break;
                                }
                            }
                            break;
                        }
                    }
                    if(testCompte)
                    {
                        System.out.println("Virement effectué avec succée");
                        return true;
                    }
                    else
                    {
                        if(testBanque)
                        {
                            throw new CompteBancaireInexistant("Compte destinataire est inexistant");
                            //System.out.println("Compte destinataire est inexistant");
                        }
                        else
                        {
                            System.out.println("il n'existe aucune banque ayant le code "+banqueiban);
                        }
                        return false;
                    }
                }
                else
                {
                    throw new DecouvertAutorise("Decouvert autorisé insufisant");
                    //System.out.println("le montant que vous avez est inferieur à la montant de transaction");
                    //return false;
                }
            }
            else
            {
                if(!debite)
                {
                    throw new DepasseDebitMax("votre compte ne vous permet pas de debiter cette quantité d'argent");
                    //System.out.println("votre compte ne vous permet pas de debiter cet quantité d'argent");
                }
                if(!etat)
                {
                    throw new EtatException("vous ne pouvez pas debiter d'un compte "+this.comptes.get(j).getEtat());
                    //System.out.println("vous ne pouvez pas debiter un compte "+this.comptes.get(j).getEtat());
                }
                if(!couran)
                {
                    throw new EpargneVirementException("vous ne pouvez pas effectuer un virement d'un compte epargne");
                    //System.out.println("vous ne pouvez pas effectuer une virement de compte epargne");
                }
                return false;
            }
            
        }
        else
        {
            System.out.println("Iban source est inexistant");
            return false;
        }
        
    }
    
    public CompteBancaireParticulier creerCompteParticulier()
    {
        Scanner sc= new Scanner(System.in);
        int choixNouveau,choixPersonne,cin,existep=0;
        boolean existe=false;
        Personne_physique personne = new Personne_physique();
        
        do
        {
            System.out.println("le titulaire de banque est un nouveau client ? (1-oui ; 2-non)");
            choixNouveau= sc.nextInt();
        }while((choixNouveau<1) || (choixNouveau>2));
        
        if(choixNouveau==1)
        {
            
            personne=personne.creerTitulaire();
            
            this.titulairesP.add(personne);
            
        }
        else
        {
            do
            {
                System.out.println("entrer le CIN de titulaire de banque");
                cin= sc.nextInt();
            
                for(int i=0; i<this.titulairesP.size();i++)
                {
                    if(this.titulairesP.get(i).getCIN().indexOf(cin)!=-1)
                    {
                        existe=true;
                        existep=i;
                        break;
                    }
                }
                if(existe)
                {
                    personne=this.titulairesP.get(existep);
                
                }
                else
                {
                    System.out.println("personne inexistant");
                }
            }while(!existe);
            
        }
        CompteBancaireParticulier particule = new CompteBancaireParticulier();
        particule.creerCompte(this.codeBanque, personne);
        boolean testex = this.comptes.stream().anyMatch(i-> i.getIban().compareTo(particule.getIban())==0);
        if(testex)
        {
            System.out.println("compte existant");
            return this.creerCompteParticulier();
        }
        else
        {
            this.comptes.add(particule);
            return particule;
        }
        
        
    }

    public CompteBancaireEntreprise creerCompteEntreprise()
    {
        Scanner sc= new Scanner(System.in);
        int choixNouveau,choixPersonne,ident,existep=0;
        boolean existe=false;
        Personne_moral personne = new Personne_moral();
        
        do
        {
            System.out.println("le titulaire de banque est nouveau client ? (1-oui ; 2-non)");
            choixNouveau= sc.nextInt();
        }while((choixNouveau<1) || (choixNouveau>2));
        
        if(choixNouveau==1)
        {
            
            personne.creerTitulaire();
            this.titulairesM.add(personne);
        }
        else
        {
            do
            {
                System.out.println("entrer l'identifiant de l'entreprise ");
                ident= sc.nextInt();
            
                for(int i=0; i<this.titulairesM.size();i++)
                {
                    if(this.titulairesM.get(i).getIdentifiantEntreprise()==ident)
                    {
                        existe=true;
                        existep=i;
                        break;
                    }
                }
                if(existe)
                {
                    personne=this.titulairesM.get(existep);
                
                }
                else
                {
                    System.out.println("personne inexistant");
                }
            }while(!existe);
            
        }
        
        CompteBancaireEntreprise entreprise = new CompteBancaireEntreprise();
        entreprise.creerCompte(this.codeBanque, personne);
        boolean testex = this.comptes.stream().anyMatch(i-> i.getIban().compareTo(entreprise.getIban())==0);
        if(testex)
        {
            System.out.println("compte existant");
            return this.creerCompteEntreprise();
        }
        else
        {
            this.comptes.add(entreprise);
            return entreprise;
        }
        
    }

    public boolean fermerCompte(String iban,DBCollection coll_compte_particulier,DBCollection coll_compte_entreprise) throws CompteBancaireInexistant
    {
        int j=0;
        boolean existe=false;
        System.out.println(iban);
        for(int i=0;i<this.comptes.size();i++)
        {
            System.out.println(this.comptes.get(i).getIban());
            if(this.comptes.get(i).getIban().compareTo(iban)==0)
            {
                j=i;
                existe=true;
                break;
            }
        }
        
        if(existe)
        {
            this.comptes.get(j).setEtat("fermer");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                String late;
        
                LocalDate today = LocalDate.now();
                String day,now ;
    
                late = LocalTime.now().format(formatter);
                day =  today.toString();
                now  = day+ " "+late;
                this.comptes.get(j).setDateMAJ(now);
                this.comptes.get(j).setDateDernierVersement(now);
                
            //mongoDB
            //update document
                    BasicDBObject updateDocument = new BasicDBObject();
                    //new value 
                    updateDocument.append("$set", new BasicDBObject()
                    .append("etat","fermer")
                    .append("dateMAJ", now)
                    );
                    //old document
                    BasicDBObject oldDocument = new BasicDBObject().append("iban", iban);
                    
                    if(this.comptes.get(j) instanceof CompteBancaireParticulier)
                    {
                        coll_compte_particulier.update(oldDocument, updateDocument,false,true);
                    }
                    else
                    {
                        coll_compte_entreprise.update(oldDocument, updateDocument,false,true);
                    }
                
            System.out.println("Fermeture de compte effectuée avec succée");
            return true;
        }
        else
        {
            throw new CompteBancaireInexistant("Compte inexistant");
            //System.out.println("Compte inexistant");
            //return false;
        }
        
    }
        
}
