import java.util.*;
import java.io.*;

public class GeneContentModel {
	private static int fastaLen = 0;
	private static int nGenes = 0;
	private static int totalGeneLen = 0;
	
	// Answers
	private double avgGeneSize = 0;
	private double geneNucleoFraction = 0;
	private double relativeGeneCoverage = 0;

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

            String gtfLine = "";
            boolean foundExon = false;
            
            int totalNumberOfNucleos = 0;
            int geneCount = 0;
            int lastNucleoNumber = -1;
            
            while(gtfScanner.hasNextLine()) {
            	
                lineScanner = new Scanner(gtfScanner.nextLine());
                // Skip seq name
                lineScanner.next();
                // Skip source
                lineScanner.next();
                // Save feature
                String feature = lineScanner.next();

                String lookingForCodon = "";
                
                // Found a gene to start working with
                if (feature.equals("stop_codon") || feature.equals("start_codon")) {
                	
                	// Increment total # gene count
                	geneCount++;
                	
                	// Skip start
                	lineScanner.next();
                	// Skip end
                	lineScanner.next();
                	// Skip score
                	lineScanner.next();
                	
                	// Save strand defined as + (forward) or - (reverse)
                	String strand = lineScanner.next();
                	if (strand.equals("+"))
                		lookingForCodon = "stop_codon";
                	else 
                		lookingForCodon = "start_codon";
                	
                	// Find the exon to figure out start range
                    foundExon = false;
                    int startGene  = -1;
                    Scanner tempScanner = null;
                    while (foundExon == false) {
                    	tempScanner = new Scanner(gtfScanner.nextLine());
                    	// Skip start
                    	tempScanner.next();
                    	// Skip end
                    	tempScanner.next();
                    	// Check feature
                    	String checkFeature = tempScanner.next();
                    	if (checkFeature.equals("exon")) {
                    		foundExon = true; 
                    		startGene = Integer.parseInt(tempScanner.next());
                    		System.out.println("Start: " + startGene);
                    	}
                    }
                    
                    // Find the start/end codon depending on the strand being forward or reverse
                    boolean foundEnd = false;
                    int endGene = -1;
                    while (foundEnd == false) {
                    	tempScanner = new Scanner(gtfScanner.nextLine());
                    	// Skip start
                    	tempScanner.next();
                    	// Skip end
                    	tempScanner.next();
                    	// Check feature
                    	String checkFeature = tempScanner.next();
                    	if (checkFeature.equals(lookingForCodon)) {
                    		foundEnd = true; 
                    		// Skip start
                    		tempScanner.next();
                    		endGene = Integer.parseInt(tempScanner.next());
                    		System.out.println("End: " + endGene);
                    	}
                    }
                    
                    // Get the number of nucleotides from the start and end range
                    int numberNucleosInGene = Math.abs(startGene - endGene);
                    System.out.println("# Nucleos in Gene: " + Math.abs(numberNucleosInGene));
                    if (endGene > lastNucleoNumber && startGene > lastNucleoNumber) {
                    	lastNucleoNumber = endGene;
                    	// Count the nucleotides, because we don't have to ignore it
                    	totalNumberOfNucleos += numberNucleosInGene;
                    	System.out.println("Total # of Nucleotides from all Genes: " + totalNumberOfNucleos);
                    }
                    else 
                    	System.out.println("-----------Ignoring this gene-----------");
                }
            }
            
            System.out.println("\n\n------------SUMMARY-------------");
            System.out.println("# of nucleotides in all genes: " + totalNumberOfNucleos);
            System.out.println("# of nucleotides in FASTA: " + fastaLen);
            System.out.println("# of valid genes in GTF: " + geneCount);
            
            // Calculate average gene size and print
            geneModel.getAverageGeneSize(totalNumberOfNucleos, geneCount);
            
            // Calculate gene nucleotide fraction and print
            geneModel.getGeneNucleotideFraction(totalNumberOfNucleos, fastaLen);
            
            // Calculate relative gene coverage and print
            geneModel.getRelativeGeneCoverage(totalNumberOfNucleos, geneCount, fastaLen);

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
            String currentLine = "";
            
            // Skip comment line
            fastScan.nextLine();
            
            while (fastScan.hasNextLine()) {
                currentLine = fastScan.nextLine();
                counter += currentLine.length();
            }
            System.out.println("Total number of nucleotides: " + counter);
            return counter;
        }
        catch(FileNotFoundException e) {
            System.out.println("Invalid FASTA file");
            return -1;
        }
    }
    
    // Calculates the average gene size 
    private double getAverageGeneSize(int totalNumberOfNucleos, int geneCount) {
        this.avgGeneSize = totalNumberOfNucleos / geneCount;
        System.out.println("\n Average gene size: " + this.avgGeneSize);
        return this.avgGeneSize;     
    }
    
    // Calculates the gene nucleotide fraction 
    private double getGeneNucleotideFraction(int totalNumberOfNucleos, int fafsaLen) {
    	this.geneNucleoFraction = totalNumberOfNucleos / fafsaLen;
        System.out.println("\n Gene nucleotide fraction: " + this.geneNucleoFraction);
		return this.geneNucleoFraction;
    }
    
    // Calculates the gene nucleotide fraction 
    private double getRelativeGeneCoverage(int totalNumberOfNucleos, int geneCount, int fastaLen) {
    	this.relativeGeneCoverage = totalNumberOfNucleos / (geneCount * fastaLen);
        System.out.println("\n Gene nucleotide fraction: " + this.relativeGeneCoverage);
		return this.relativeGeneCoverage;
    }
}