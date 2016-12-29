<?php
	/* ETAPE 0 : TINCLUDE DE FONCTIONS ET PARAMETRAGE */
	$GLOBALS['json']=1;
	include('inc/erreurs.inc');



	/* ETAPE 2 : CONNEXION A LA BASE DE DONNEES */
	include('inc/db.inc');



	/* ETAPE 3 : RECUPERATION DE LA LISTE DES JEUX */
	// 3.1. On exécute la requête
	try{
		$requete="SELECT jeu, pseudo, MAX( score ) AS maxScore
							FROM utilisateurs AS U
							INNER JOIN scores AS S ON U.id_utilisateur = S.id_utilisateur
							GROUP BY jeu, pseudo HAVING maxScore IN((SELECT MAX(score) FROM scores WHERE scores.jeu = S.jeu))";
		$stm= $bdd->prepare($requete);
		$stm->execute();
	}catch(Exception $e){
		RetournerErreur(2003);
	}

	// 3.2. On vérifie si on a trouvé au moins un jeu
	if($row = $stm->fetch()){
		$chaine_jeux = '{"nom_jeu": "' . $row["jeu"] . '","pseudo": "' . $row["pseudo"] . '"}';
	}else{
		RetournerErreur(300);
	}

	// 3.3. On construit le fichier JSON des jeux
	while ($row = $stm->fetch()) {
		$chaine_jeux .= ', {"nom_jeu": "' . $row["jeu"] . '","pseudo": "' . $row["pseudo"] . '"}';
	}


	/* ETAPE 4 : SI ON EST ARRIVE JUSQU'ICI, C'EST QUE TOUT EST CORRECT */
	$resultat='{ "code": 0, "jeux": [' . $chaine_jeux . '] }';
	echo $resultat;


	/* Valeurs de retour
	 * 00 : OK
	 * 300 : Aucun jeu trouvé
	 * 1000 : problème de connexion à la DB
	 * 20XX : autre problème
	 */
?>
