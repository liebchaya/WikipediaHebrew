package morphology;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import vohmm.application.SimpleTagger3;
import vohmm.corpus.AffixFiltering;
import vohmm.corpus.Sentence;
import vohmm.corpus.TokenExt;
import vohmm.lexicon.BGULexicon;

/**
 * BGU POS-tagger 
 * @author HZ
 *
 */
public class Tagger {
	private static SimpleTagger3 m_tagger;
	
	/**
	 * Initiates the tagger
	 * @param taggerHomdir
	 * @throws Exception
	 */
	public static void init(String taggerHomdir) throws Exception{  
		m_tagger = new SimpleTagger3(taggerHomdir,vohmm.application.Defs.TAGGER_OUTPUT_FORMAT_BASIC,false,false,false,false,null,AffixFiltering.NONE);
		BGULexicon._bHazal = true;
	}
	
	/**
	 * Tags a string
	 * @param str
	 * @return list of tagged sentences
	 * @throws Exception
	 */
	private static List<Sentence> getTaggedSentences(String str) throws Exception  {
		return m_tagger.getTaggedSentences(str);
	}
	
	/**
	 * Gets the most probable lemma, supports ngrams
	 * @param str
	 * @return a set - containing the most probable lemma - a single string
	 * @throws Exception
	 */
	public static Set<String>  getTaggerLemmas(String str) throws Exception{
		HashSet<String> lemmas = new HashSet<String>();
		String lemma = "";
		for (Sentence sentence : getTaggedSentences(str)) {
			for (TokenExt token : sentence.getTokens()) {
				lemma = lemma + " " + token._token.getSelectedAnal().getLemma().getBaseformStr();
		 }
		}
		if (!lemma.equals(""))
			lemmas.add(lemma.trim());
		return lemmas;
	}
	
	/**
	 * Gets the most probable lemma, supports ngrams
	 * @param str
	 * @return a set - containing the most probable lemma - a single string
	 * @throws Exception
	 */
	public static List<String>  getTaggerLemmasList(String str) throws Exception{
		List<String> lemmas = new ArrayList<String>();
		for (Sentence sentence : getTaggedSentences(str)) {
			for (TokenExt token : sentence.getTokens()) {
				lemmas.add(token._token.getSelectedAnal().getLemma().getBaseformStr());
		 }
		}
		return lemmas;
	}
	
	/**
	 * This function is not in use,
	 * to support this functionality see ResponsaNewSystem.morphology.MorphLemmatizer
	 * @param str
	 * @return set of lemmas
	 * @throws Exception
	 */
	@Deprecated
	public static Set<String>  getAllPossibleLemmas(String str) throws Exception{
		HashSet<String> lemmas = new HashSet<String>();
		return lemmas;
	}
	

}
