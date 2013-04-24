# ------------------------------------------------------------------
# Computes the relatedness of two elements and gives the lesk score.
# http://search.cpan.org/dist/WordNet-Similarity/lib/WordNet/Similarity/lesk.pm
# ------------------------------------------------------------------


use WordNet::QueryData;
use WordNet::Similarity::lesk;


my $wn = WordNet::QueryData->new();
my $lesk = WordNet::Similarity::lesk->new($wn);
my %englishStopWords = (); # Dictionary of English stop words
my %dictionary = (); # Dictionary of the words present in the texts
my $totalWords = 0;

open INFILE, "edaena.txt" or die $!;
my @lines = <INFILE>;

my $phrase = "";
my $isLiterally;

readStopWords(); # Populate englishStopWords dictionary.
readDictionary(@lines); # Populate the dictionary of words present in the file.



foreach (@lines) {
    my $first_char = substr($_, 0, 1);
 	my $word;
 	
    if ($first_char eq '@')
    {   
        $phrase = substr($_, 2);
        chomp($phrase);
    }
    elsif ($first_char eq '$')
    {
        my $sense = substr($_, 2, length($_));
        chomp($sense);
        if ($sense eq "literally") { $isLiterally = 1; }
        else { $isLiterally = 0; }
    }
    else
    {
        $tempPhrase = $phrase;
        my @context = split(/ /, $_);
        my @words;
     
        foreach (@context) {
            my @word = split(/#/, $_);
            #print $word[0] . " ";
            if (exists $englishStopWords{$word[0]} )
            {
 	            #print $_ . "\n";
 	         }
 	        
        } 
        
        if ($isLiterally == 1) 
        {
            $tempPhrase =~ s/\s+/_/g;
            removeStopWords($phrase);
            if (hasWordnetSense($tempPhrase)) { 
                $phrase = $tempPhrase; # if the phrase has a sense use that sense.
            }
            else { $phrase = removeStopWords($phrase); }
        }
        else { $phrase = removeStopWords($phrase); }
        
     } # else
}

my $relatedness = $lesk->getRelatedness("car#n#1", "bus#n#2");

($error, $errorString) = $lesk->getError();

die "$errorString\n" if($error);




#print "$_\n" for keys %englishStopWords;

#print "car (sense 1) <-> bus (sense 2) = $relatedness\n";

# ------------
# Subroutines
# ------------
sub hasWordnetSense
{
   $p = $_[0];
   my @words = split(/_/, $p);
   
   if ($words[0] eq "a") 
   {
        $p = substr($p, 2);
   }
   return $wn->querySense($p);
}

sub readStopWords
{
    #print "hello\n";
    open FILE, "data/stopwords.english" or die $!;
    my @lines = <FILE>;
    foreach (@lines) 
    {
        $word = $_;
        chomp($word);
        $englishStopWords{$word} = 1;
    }
    close FILE;
    if (exists $englishStopWords{"a"}) {
        #print "**** IN exists\n";
    }
}

sub readDictionary
{
    @lines = @_;
    foreach (@lines) {
        my $first_char = substr($_, 0, 1);
 	my $word;
    
        if ($first_char ne '@' and $first_char ne '$')
        {   
            $phrase = substr($_, 2);
            chomp($phrase);
            
            $tempPhrase = $phrase;
            my @words = split(/ /, $_);
     
            foreach (@words) 
            {
                my @word = split(/#/, $_);
                my $word = lc($word[0]);
                chomp($word);
                if ($word ne "" and not(exists $englishStopWords{$word}) and not (exists $dictionary{$word}) )
                {
                    $dictionary{$word} = $totalWords;
                    $totalWords = $totalWords + 1;
                }
            } 
         }

    }
    #print @lines;
}

sub isStopWord
{
    return exists $englishStopWords{$_[0]};
}

sub removeStopWords
{
    @words = split(/ /, $_[0]);
    $words = "";
    foreach (@words)
    {
        if (not isStopWord($_))
        {
            $words = $words . $_ . " ";
        }
    }
    chomp($words);
    return $words;
}


=pod
print "$dictionary{$_} - $_\n" for keys %dictionary;

print $totalWords;
print "\n";
print length(%dictionary);
print "\n";
=cut


#print "\n";



# if (exists $strings{$string}) {

