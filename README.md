# ProjetApplication3ASTI_poussa-poussi# Le Poussa-Poussi

## Introduction
Le poussa-poussi est un jeu abstrait de stratégie combinatoire pour deux personnes.

## Matériel
Le jeu contient :
- Un plateau de 64 cases
- 48 jetons blancs
- 48 jetons noirs

Les jetons ont la même taille que les cases du plateau.

## But du jeu
Le but du jeu est de marquer 2 points avant son adversaire. Un joueur marque un point lorsqu’il parvient à aligner 5 pièces de sa couleur.

## Début de partie
Au début de la partie, aucune pièce n’est sur le plateau. Chaque joueur se voit attribuer une couleur (blanc ou noir) et reçoit les pièces correspondantes. On tire au sort le joueur qui commence.

## Déroulement
Chaque joueur joue à tour de rôle; le tour se déroule en 2 phases :

### Phase 1 (obligatoire)
Le joueur pose une pièce de sa couleur sur une case libre du plateau. Cette pièce doit être adjacente au bord du plateau ou à une autre pièce (les deux pièces doivent être en contact sur un de leurs bords, et pas seulement sur un coin).


### Phase 2 (facultative)
Le joueur peut pousser une pièce de sa couleur se trouvant sur le plateau (verticalement ou horizontalement). La pièce poussée entraîne les pièces qui sont alignées avec elle sans espace vide dans la direction de la poussée. La poussée s’arrête dès que l’alignement de pièces poussé rencontre une autre pièce ou le bord du plateau (on ne peut pas arrêter la poussée avant cela).


**Attention :** Il est interdit d'effectuer une poussée si toutes les pièces déplacées se retrouvent sur les cases qu'elles occupaient au tour précédent de l'adversaire.

## Marquer un point
Lorsque 5 pièces de la même couleur sont alignées (horizontalement, verticalement ou en diagonale), le joueur correspondant marque un point. Le joueur doit alors retirer du plateau deux pièces de cette ligne.

**Note :** Si les deux joueurs créent  lignes sont créées dans un même tour, aucun joueur ne marque de point. C'est d'abord le joueur qui a créé les lignes qui enlève ses pièces.

---

Ce document décrit les règles essentielles pour jouer au Poussa-Poussi. Profitez du jeu et bonne chance!
