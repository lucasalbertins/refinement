package com.ref.wellformedness;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ref.exceptions.WellFormedException;
import com.ref.interfaces.activityDiagram.IActivityDiagram;
import com.ref.interfaces.activityDiagram.IObjectNode;

public class ObjectDefinitionWellFormedness {

	ObjectDefinitionWellFormedness() {

	}

	public static void wellFormed(IActivityDiagram act, ArrayList<IObjectNode> objNodes) throws WellFormedException {
		// Checando se exiset algum object node no diagrama
		if (!(objNodes.isEmpty())) {
			// criando metodo de busca REGEX para isolar os tipos definidos nas definições do diagrama
			List<String> definedTypes = new ArrayList<String>();
			Matcher m = Pattern.compile("(\\w*)=\\{[a-z|0-9]*\\.\\.[0-9|a-z]*\\};").matcher(act.getDefinition());

			while (m.find()) {
				definedTypes.add(m.group(1));// adicionando tipos achados a um array de strings
			}
			if (definedTypes.isEmpty()) {// caso não haja nenhum tipo válido
				throw new WellFormedException("No data types in the object nodes");
			} else {
				for (IObjectNode obj : objNodes) {
					if (obj.getBase() == null) {// caso exista algum object node sem tipo definido
						throw new WellFormedException("Unspecified data type (" + obj.getName() + ")");
					}
				}
			}
		}
	}
}
