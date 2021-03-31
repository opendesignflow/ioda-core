package org.odfi.ioda.ui.color;

import net.mahdilamb.colormap.Colormaps;

import java.util.LinkedHashSet;
import java.util.stream.Collectors;

public class ColorsmapLister {

    public static java.util.Set<String> listDefaultMaps() {
        return Colormaps.named().stream().map(v -> ((CharSequence) v).toString()).sorted().collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
