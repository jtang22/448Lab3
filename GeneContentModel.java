import java.util.*;
import java.io.*;

public class GeneContentModel {
	private static int fastaLen = 0;
	private static int nGenes = 0;
	private static int totalGeneLen = 0;

	public static void main(String[] args) {
		Scanner gtfScanner, lineScanner  = null;
        GeneContentModel geneModel = new GeneContentModel();
		File gtfFile;
		File fastaFile;

        try {
            gtfFile = new File("Dere3L1_gtf.txt");
            fastaFile = new File("Dere3L1_FASTA.txt");
            gtfScanner = new Scanner(gtfFile);

            fastaLen = geneModel.calcNucleotides(fastaFile);

            String gtfLine;
            while(gtfScanner.hasNextLine()) {
                lineScanner = new Scanner(gtfScanner.nextLine());
                lineScanner.next();
                lineScanner.next();
                String feature = lineScanner.next();

                if (feature.equals("stop_codon") || feature.equals("start_codon")) {
                    int startGene = lineScanner.nextInt();


                }
            }
		}
        catch(FileNotFoundException e) {
            System.out.println("Invalid GTF File");
        }
	}

    //Counts number of nucleotides in Fasfa file
    private int calcNucleotides(File fasta) {
        Scanner fastScan;
        int counter = 0;
        try {
            fastScan = new Scanner(fasta);
            fastScan.nextLine();
            while (fastScan.hasNextByte()) {
                fastScan.nextByte();
                counter++;
            }
            System.out.println(counter);
            return counter;
        }
        catch(FileNotFoundException e) {
            System.out.println("Invalid FASTA file");
            return -1;
        }
    }

}