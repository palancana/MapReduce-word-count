// Rafael Díaz Rodríguez 1212588
// Mario Arias Escalona  1362363


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;



public class WordCountMultiThread
{

	//Sets number of threds to the quantity of cores that has the computer
	private static int THREAD_COUNT = Runtime.getRuntime().availableProcessors();

    private static class FileIterator implements Iterator, AutoCloseable
    {
        private final BufferedReader br;
        private String nextLine;


        public FileIterator( String fileName ) throws IOException
        {
            br = new BufferedReader( new FileReader( fileName ) );
            nextLine = br.readLine();
        }


        public boolean hasNext()
        {
            return nextLine != null;
        }


        public String next()
        {
            String lineToReturn = nextLine;
            try {
                nextLine = br.readLine();
            } catch ( IOException e ) {
                nextLine = null;
            }
            return lineToReturn;
        }

        public void close() throws IOException
        {
            br.close();
        }
    }


    private static class Map_Reduce
    {
    	public String[] map(String input){
    		String[] words=input.split( "[ _\\,\\-\\+\\.]" );
            List<String> filteredList = new ArrayList<>();
            for ( String word : words ) {
                if ( word.matches( "[a-zA-Z_0-9_!_-_' (?U)\\p{L}*]+" ) ) {
                    filteredList.add( word );
                }
            }
            String[] legalWords=filteredList.toArray( new String[filteredList.size()] );
            
            String[] filteredList_final = new String[legalWords.length];
            for ( int i = 0; i < legalWords.length; i++ ) {
                filteredList_final[i] = legalWords[i].toLowerCase();
            }
            return filteredList_final;
    		
    	}
    	
    	
        public synchronized void reduce( Map<String, Integer> counter, String word )
        {
            if ( counter.containsKey( word ) ) {
                counter.put( word, counter.get( word ) + 1 );
            } else {
                counter.put( word, 1 );
            }
        }
    }


    private static class Map_RedcueThread implements Runnable
    {
        private Map_Reduce tr;
        private Queue<String> dataQueue;
        private Map<String, Integer> counters;


        public Map_RedcueThread( Map_Reduce tr, Map<String, Integer> counters, Queue<String> dataQueue )
        {
            this.tr = tr;
            this.dataQueue = dataQueue;
            this.counters = counters;
        }


        @Override public void run()
        {
            while ( !dataQueue.isEmpty() ) {
                String line = dataQueue.poll();
                if ( line != null ) {
                    String[] lowerCaseWords = tr.map( line );
                    for ( String word : lowerCaseWords ) {
                        tr.reduce( counters, word );
                    }
                }
            }
        }
    }


    public static void main( final String[] args ) throws Exception
    {
    	Map_Reduce MP = new Map_Reduce();
        Map<String, Integer> counters = new HashMap<>();
        final Queue<String> dataQueue = new ConcurrentLinkedQueue<>();
        
        new Thread()
        {
            public void run()
            {	
            	
            	
                try ( FileIterator fc = new FileIterator( args[0] ) ) {
                    while ( fc.hasNext() ) {
                        dataQueue.add( fc.next() );
                        
                    }
                } catch ( IOException e ) {
                    e.printStackTrace();
                }
            
            }
        }.start();
        while ( dataQueue.isEmpty() ) {
            
            Thread.sleep( 10 );
        }
        ExecutorService es = Executors.newFixedThreadPool( THREAD_COUNT );
        for ( int i = 0; i < THREAD_COUNT; i++ ) {
            es.execute( new Map_RedcueThread( MP, counters, dataQueue ) );
            
        }
        es.shutdown();
        es.awaitTermination( 1, TimeUnit.MINUTES );  

        //The file name is printed in the script
        Map<String, Object> sortedHashMap = new TreeMap<String, Object>(counters);
        for (Map.Entry<String, ?> entry : sortedHashMap.entrySet()) {   
        	
        	  System.out.println("      "+entry.getValue() + " " + entry.getKey());        	 
        	}
    }
}
