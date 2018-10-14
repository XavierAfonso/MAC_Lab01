package ch.heigvd.iict.dmg.labo1.similarities;

import org.apache.lucene.search.similarities.ClassicSimilarity;

public class MySimilarity extends ClassicSimilarity {

    /*public float tf(float freq){

        float result = (float) (1 + Math.log(freq));
        return result;
    }

    public float idf(long docFreq, long numDocs){

        float result = (float) (Math.log(numDocs/(docFreq+1))+1);
        return result;
    }

    public float coord(int overlap,int maxOverlap){

        float result = (float) Math.sqrt(overlap/maxOverlap);
        return result;
    }

    public float lengthNorm(FieldInvertState state){
        return 1f;
    }*/
}
