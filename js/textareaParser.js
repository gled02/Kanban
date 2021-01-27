/*
 * Code javascript afin de parser un texte et permettre une mise en forme
 * 	Ce parser contient 2 balises :
 * 		**  --> met un texte en gras
 * 		__  --> souligne un texte
 */

// Cette fonction permet de transformer un texte avec les balises en code HTML
function parseText(text) {
	var parsedText = text;
	
	if (((text.match(/\*\*/g) || []).length % 2) == 0) {
		var strongTag = "**";
		var tmp = ""
		var textStrong = parsedText.split(strongTag);
		for (i = 1; i < textStrong.length; i += 2) {
			tmp += textStrong[i - 1];
			tmp += "<span style='font-weight: bold;'>";
			tmp += textStrong[i];
			tmp += "</span>";
		}
		tmp += textStrong[textStrong.length - 1];
		parsedText = tmp;
	}
	
	if (((text.match(/\_\_/g) || []).length % 2) == 0) {
		var strongTag = "__";
		var tmp = ""
		var textStrong = parsedText.split(strongTag);
		for (i = 1; i < textStrong.length; i += 2) {
			tmp += textStrong[i - 1];
			tmp += "<span style='text-decoration: underline;'>";
			tmp += textStrong[i];
			tmp += "</span>";
		}
		tmp += textStrong[textStrong.length - 1];
		parsedText = tmp;
	}
	
	return parsedText;
}

// Cette fonction permet de transformer un code HTML en texte avec les balises
function placeTagText(text) {
	var replacedText = text;
	var nbBold = (text.match(/<span style="font-weight: bold;">/g) || []).length
	var nbUnderline = (text.match(/<span style="text-decoration: underline;">/g) || []).length;
	for (i = 0; i < nbBold; ++i) {
		replacedText = replacedText.replace('<span style="font-weight: bold;">', "**");
		replacedText = replacedText.replace("</span>", "**");
	}
	for (i = 0; i < nbUnderline; ++i) {
		replacedText = replacedText.replace('<span style="text-decoration: underline;">', "__");
		replacedText = replacedText.replace("</span>", "__");
	}
	return replacedText;
}