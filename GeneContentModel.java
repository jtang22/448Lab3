import java.util.*;
import java.io.*;
import java.text.DecimalFormat;

public class GeneContentModel {
	private static int fastaLen = 0;
	private static int nGenes = 0;
	private static int totalGeneLen = 0;
	
	// Answers
	private double avgGeneSize = 0;
	private double geneNucleoFraction = 0;
	private double relativeGeneCoverage = 0;

    private Writer csvFileWriter, txtFileWriter;
    private BufferedWriter csvBufferedWriter, txtBufferedWriter;

    private File gtfFile, fastaFile;
    private String seqName;

    public GeneContentModel() {
        reset();
    }

	public int caclulateGeneContent(File gtfFile, File fastaFile) {
		Scanner gtfScanner, lineScanner  = null;
		
        try {
            //output files
            String outputFile = gtfFile.getName().replace (".txt", ("_genes.csv"));
            csvFileWriter = new FileWriter(outputFile);
            csvBufferedWriter = new BufferedWriter(csvFileWriter);

            outputFile = gtfFile.getName().replace(".txt", ("_calc.txt"));
            txtFileWriter = new FileWriter(outputFile);
            txtBufferedWriter = new BufferedWriter(txtFileWriter);

            gtfScanner = new Scanner(gtfFile);

            fastaLen = calcNucleotides(fastaFile);

            String gtfLine = "";
            boolean foundExon = false;
            
            int totalNumberOfNucleos = 0;
            int geneCount = 0;
            int lastNucleoNumber = -1;
            
            while(gtfScanner.hasNextLine()) {
            	
                lineScanner = new Scanner(gtfScanner.nextLine());
                
                // Skip seq name
                seqName = lineScanner.next();
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
                    while (foundExon == false && gtfScanner.hasNextLine()) {
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
                   		}
                    }
                    
                    // Find the start/end codon depending on the strand being forward or reverse
                    boolean foundEnd = false;
                    int endGene = -1;
                    while (foundEnd == false && gtfScanner.hasNextLine()) {
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
                   		}
                    }
                    
                    // Get the number of nucleotides from the start and end range
                    int numberNucleosInGene = Math.abs(startGene - endGene);
                    if (endGene > lastNucleoNumber && startGene > lastNucleoNumber) {
                    	lastNucleoNumber = endGene;
                    	// Count the nucleotides, because we don't have to ignore it
                    	totalNumberOfNucleos += numberNucleosInGene;
                        csvBufferedWriter.write(geneCount + ", " + startGene + ", " + endGene);
                        csvBufferedWriter.newLine();
                		//System.out.println("Start: " + startGene);
                		//System.out.println("End: " + endGene);
                        System.out.println("# of Nucleotides in gene: " + Math.abs(numberNucleosInGene));
                    	System.out.println("Total # of Nucleotides from all genes: " + totalNumberOfNucleos);
                    }
                    else 
                    	System.out.println("-----------Ignoring overlapping gene-----------");
                }
            }
            
            txtBufferedWriter.write("Sequence Name: " + seqName);
            txtBufferedWriter.newLine();
            txtBufferedWriter.write("Number of Genes: " + geneCount);
            txtBufferedWriter.newLine();
            txtBufferedWriter.write("Total Length of Sequence: " + fastaLen);
            txtBufferedWriter.newLine();
            txtBufferedWriter.write("Total Length of Nucleotides in Genes: " + totalNumberOfNucleos);
            txtBufferedWriter.newLine();
            // System.out.println("\n\n------------SUMMARY-------------");
            // System.out.println("# of nucleotides in all genes: " + totalNumberOfNucleos);
            // System.out.println("# of nucleotides in FASTA: " + fastaLen);
            // System.out.println("# of valid genes in GTF: " + geneCount);
            
            // Calculate average gene size and print
            double avgFraction = getAverageGeneSize(totalNumberOfNucleos, geneCount);
            txtBufferedWriter.write("\nAverage gene size: " + avgFraction);
            txtBufferedWriter.newLine();
            
            // Calculate gene nucleotide fraction and print
            double geneNucFraction = getGeneNucleotideFraction(totalNumberOfNucleos, fastaLen);
            DecimalFormat formatter = new DecimalFormat("0.000");
            txtBufferedWriter.write("\nGene nucleotide fraction: " + formatter.format(geneNucFraction) + "%");
            txtBufferedWriter.newLine();
            
            // Calculate relative gene coverage and print
            double relativeGene = getRelativeGeneCoverage(totalNumberOfNucleos, geneCount, fastaLen);

            txtBufferedWriter.write("\nRelative gene coverage: " + formatter.format(relativeGene) + "%");
            txtBufferedWriter.newLine();

            csvBufferedWriter.close();
            txtBufferedWriter.close();
            csvFileWriter.close();
            txtFileWriter.close();
            
            return 0;
		}
        catch(FileNotFoundException e) {
            return 1;
        }
        catch(IOException e) {
            return 1;
        }
	}

    public void reset() {
        fastaLen = 0;
        nGenes = 0;
        totalGeneLen = 0;
        avgGeneSize = 0;
        geneNucleoFraction = 0;
        relativeGeneCoverage = 0;
        csvFileWriter = null;
        txtFileWriter = null;
        csvBufferedWriter = null;
        txtBufferedWriter = null;
        gtfFile = null;
        fastaFile = null;
        seqName = "";
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
            System.out.println("----------------------------------------");
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
        System.out.println("\nAverage gene size: " + this.avgGeneSize);
        return this.avgGeneSize;     
    }
    
    // Calculates the gene nucleotide fraction 
    private double getGeneNucleotideFraction(int totalNumberOfNucleos, int fafsaLen) {
    	this.geneNucleoFraction = totalNumberOfNucleos / (double) fafsaLen;
		double fraction = this.geneNucleoFraction * 100;

		DecimalFormat formatter = new DecimalFormat("0.000");
		System.out.println("Gene nucleotide fraction: " + formatter.format(fraction) + "%");
		return fraction;
    }
    
    // Calculates the gene nucleotide fraction 
    private double getRelativeGeneCoverage(int totalNumberOfNucleos, int geneCount, int fastaLen) {
    	this.relativeGeneCoverage = totalNumberOfNucleos / ((double)geneCount * (double)fastaLen);
		double fraction = this.relativeGeneCoverage * 100;
		
		DecimalFormat formatter = new DecimalFormat("0.000");
        System.out.println("Relative gene coverage: " + formatter.format(fraction) + "%");
		return fraction;
    }
}