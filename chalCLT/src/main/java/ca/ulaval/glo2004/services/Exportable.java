package ca.ulaval.glo2004.services;

import ca.ulaval.glo2004.services.CaisseOutils.*;

public interface Exportable {

    ContenuSTL genererVersionBrut(String fileDestination, Triplet<UniteImperiale, UniteImperiale, UniteImperiale> p_chaletDimmension);

    ContenuSTL genererVersionFini(String fileDestination, Triplet<UniteImperiale, UniteImperiale, UniteImperiale> p_chaletDimmension);

    ContenuSTL genererVersionRetrait(String fileDestination, Triplet<UniteImperiale, UniteImperiale, UniteImperiale> p_chaletDimmension, int sequentiel);

}