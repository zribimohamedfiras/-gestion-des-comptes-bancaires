/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gestionbanque;

import com.mongodb.DBCollection;

/**
 *
 * @author firas
 */
public interface OrganismeBancaire {
    public boolean debiterCompte(String iban, double montant,DBCollection coll_compte_particulier,DBCollection coll_compte_entreprise)throws CompteBancaireInexistant,DecouvertAutorise,DepasseDebitMax,EtatException;
    public boolean crediterCompte(String iban, double montant,DBCollection coll_compte_particulier,DBCollection coll_compte_entreprise)throws CompteBancaireInexistant;
    public boolean virerArgent(String iban_source,String iban_destination, double montant,DBCollection coll_compte_particulier,DBCollection coll_compte_entreprise) throws CompteBancaireInexistant,DecouvertAutorise,DepasseDebitMax,EtatException,EpargneVirementException;
    public CompteBancaireParticulier creerCompteParticulier();
    public CompteBancaireEntreprise creerCompteEntreprise();
    public boolean fermerCompte(String iban,DBCollection coll_compte_particulier,DBCollection coll_compte_entreprise)throws CompteBancaireInexistant;
    
}
