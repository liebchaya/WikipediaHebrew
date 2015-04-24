package test;

import morphology.Tagger;

public class TestTagger {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		Tagger.init("F:\\BGUTaggerData\\");
		System.out.println(Tagger.getTaggerLemmasList("thumb|��"));
		System.out.println(Tagger.getTaggerLemmasList("�����������������������������������������������������"));
		System.out.println(Tagger.getTaggerLemmasList("������"));

	}

}
