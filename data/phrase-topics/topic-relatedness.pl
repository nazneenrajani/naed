use WordNet::QueryData;
use WordNet::Similarity::vector;
use WordNet::SenseRelate::TargetWord; # op2
use WordNet::SenseRelate::WordToSet;
use WordNet::SenseRelate::Word;
use WordNet::SenseRelate::Algorithm::SenseOne;
use WordNet::SenseRelate::Word;

my $wn = WordNet::QueryData->new();
my $querydata = WordNet::QueryData->new;

my $vector = WordNet::Similarity::vector->new($wn);
my $wntools = (preprocess => [],
                           preprocessconfig => [],
                           context => 'WordNet::SenseRelate::Context::NearestWords',
                           contextconfig => {(windowsize => 5,
                                              contextpos => 'n')},
                           algorithm => 'WordNet::SenseRelate::Algorithm::SenseOne',
                           algorithmconfig => {(measure => 'WordNet::Similarity::res')});

my $tool = WordNet::SenseRelate::Algorithm::SenseOne->new($wntools); #WordNet::SenseRelate::SenseOne->new();

my %wsd_options = (preprocess => [],
                           preprocessconfig => [],
                           context => 'WordNet::SenseRelate::Context::NearestWords',
                           contextconfig => {(windowsize => 5,
                                              contextpos => 'n')},
                           algorithm => 'WordNet::SenseRelate::Algorithm::Local',
                           algorithmconfig => {(measure => 'WordNet::Similarity::res')});

        # Initialize the object
my %options = (wordnet => $querydata,
                      measure => 'WordNet::Similarity::vector');

       my $wsd = WordNet::SenseRelate::WordToSet->new (%options);

        #my ($wsd, $error) = WordNet::SenseRelate::TargetWord->new(\%wsd_options, 0);

my $type = $ARGV[0];

my $topic_path = "/Users/edaena/Documents/SPRING2013/naed/data/phrase-topics/topics/";
my $context_path = "/Users/edaena/Documents/SPRING2013/naed/data/phrase-topics/phrases/np-senses-$type/"; # np senses (no-phrase)
my @literal_topics; 
my @figurative_topics;

my @literal_topic_senses;
my @figurative_topic_senses;

my @l_topic_senses;
my @f_topic_senses;

#my @contexts = ();
my $phrase_num = $ARGV[1];#4;
my %stopWords = ();

# Retrieve topics for each phrase.
readStopWords();
#while ($phrase_num <= $phrase_num)
#{
    my @t = ();
    my $path = $topic_path . $phrase_num . ".txt";
    my $isFigurative = 1;
    #print $path;
    
    
    @literal_topics = readTopics("$phrase_num-literal");
    @figurative_topics = readTopics("$phrase_num-figurative");
    
    
    foreach my $t (@literal_topics)
    {
        my @a = getAllSenses($t);
        push(@l_topic_senses, \@a);
    }
    foreach my $t (@figurative_topics)
    {
        my @a = getAllSenses($t);
        push(@f_topic_senses, \@a);
    }
    
    
    #@l_topic_senses = getAllTopicSenses(\@literal_topics);
    #@f_topic_senses = getAllTopicSenses(\@figurative_topics);
    
    
    my $cp = $context_path . $phrase_num . ".txt";
    #print "$cp\n";

    open IN, ($context_path . $phrase_num . ".txt") or die $!;
    my @lines = <IN>;
    
    #open(my $OUT, '>', "/Users/edaena/Documents/SPRING2013/naed/data/phrase-topics/$phrase_num-results.txt") or die "Ouch: $!";
    open(my $OUT, '>', "/Users/edaena/Documents/SPRING2013/naed/data/phrase-topics/np-results/new/$phrase_num-results-$type-new.txt") or die "Ouch: $!";
    close $OUT;
    foreach my $l (@lines)
    {
        my $first_char = substr($l, 0, 1);
        my $word;
        
        if ($first_char ne '@')
        {   
            if ($first_char eq '$')
            {
                my $sense = substr($l, 2, length($l));
                chomp($sense);
                if ($sense eq "figuratively") { $isFigurative = 1; }
                else { $isFigurative = 0; }
            }
            elsif (length($l) > 1) # ignore blank lines
            {
                open(my $OUT, '>>', "/Users/edaena/Documents/SPRING2013/naed/data/phrase-topics/np-results/new/$phrase_num-results-$type-new.txt") or die "Ouch: $!";
                my @context = split(/ /, $l); # Words that are going to be compared against the topics
                @context = removeInvalidWord(@context);
                my $size = @context;

                    my @formatted_context = removeStopWords(\@context);#formatContext(\@context);
                    #print "@formatted_context\n";
    
                    # getTopicSenses
                   # @literal_topic_senses = getTopicSenses(\@literal_topics, \@formatted_context);
                   # @figurative_topic_senses = getTopicSenses(\@figurative_topics, \@formatted_context);
                    
                    #print "Size = $s\n";
                    
                    #print "Size = $s\n";
                    #print "Computing context to topic relatedness...\n";
                   
                    # compute topic to context relatedness
                    my $s = @figurative_topics;
                    # print "senses Size = $s\n";
                    # print "** @figurative\n";
                    print "LITERAL\n";
                    my $literal_relatedness = getContextToTopicRelatedness(\@literal_topics, \@context, \@l_topic_senses);
                    print "literal relatedness: $literal_relatedness\n";
                    print "FIGURATIVE\n";
                    my $figurative_relatedness = getContextToTopicRelatedness(\@figurative_topics, \@context, \@f_topic_senses);
                    print "figurative relatedness: $figurative_relatedness\n";
                    my $answer = 1; # figurative
                    if ($literal_relatedness > $figurative_relatedness) { $answer = 0 }
                    
                    print "$isFigurative $answer $literal_relatedness $figurative_relatedness\n";
                    #print $OUT "@context\n";
                    print $OUT "$isFigurative $answer $literal_relatedness $figurative_relatedness\n";
                    $OUT->autoflush(1);



            } # else
        } #if
        close $OUT;
    }
    
    
    close IN;
    

    # Read context
    #@contexts = readContexts("$phase_num");
    # Compute sense of the topic word, word to word sense
    # Compare each word of the context to each topic
    

    $phrase_num = $phrase_num + 1;
#}

#getRelatedness(); #("eat", "cake#n#1");

# topic words senses based on each of the contexts
# getrelatedness of each word from the list of topics to the ones from the contexts

# Get context to topic relatedness
sub getContextToTopicRelatedness #@topics @topic_senses, @context
{ # receive bidimensional array with all senses of each topic
    print "Computing topic context relatedness...\n";
    my ($topic_sense_ref, $words_ref, $senses_ref) = @_;
    my @topics = @{$topic_sense_ref};
    my @words = @{$words_ref};
    my @senses = @{$senses_ref};
    my $context_size = @words;
    my $topics_size = @topics;
    
    
    my $global_result = 0;
    foreach my $word (@words)
    {
        my $result = 0;
        
        
        foreach my $topic_s (@senses)
        {
            my @arr = @$r;
            my @list_senses = @$topic_s;
            my $max_value = 0;
            foreach my $t_sense (@list_senses)
            {
                my $r = getRelatedness($word, $t_sense);
                if ($r > $max_value) { $max_value = $r; }
            }
            $result = $result + $max_value;
        }
        $result = ($result * 1.0) / $topics_size;
        $global_result = $global_result + $result;
=pod       
        foreach my $t (@topics)
        {
            my @t_senses = getAllSenses($t);
            
            # compare relatedness against all senses and pick the highest
            my $max_value = 0;
            foreach my $ts (@t_senses)
            {
                my $r = getRelatedness($word, $ts);
                if ($r > $max_value) { $max_value = $r; }
            }
            $result = $result + $max_value;
            
            #$result = $result + getRelatedness($word, $t);
            # print "$word $t: $result\n";
        }
        $result = ($result * 1.0) / $topics_size;
        $global_result = $global_result + $result;
=cut
    }
    $global_result = ($global_result * 1.0) / $context_size;
    return $global_result;
}

# Get the senses of a list of topics based on a context
sub getTopicSenses #@topics, @context(words)
{
    print "Computing topic senses...\n";
    my ($topic_ref, $words_ref) = @_;
    my @topics =  @{$topic_ref};
    my @words =  @{$words_ref};
    my @senses = ();
    my $size = @words;
    
    if ($size > 0)
    {
        foreach my $t (@topics)
        {
            chomp($t);
            my $sense = getSenseFromContext($t, \@words);
            push(@senses, $sense);
        }
        #print "here1 @senses\n";
        print "Done topic senses.\n";
    }
    return @senses;
}



sub getAllSenses # word#pos
{
    my $word = shift;
    my $pos;
    my $sense;
    my $key;
    my @senses;
    return () if(!defined $word);
    
    # First separately handle the case when the word is in word#pos or
    # word#pos#sense form.
    if($word =~ /\#/)
    {
    if($word =~ /^([^\#]+)\#([^\#])\#([^\#]+)$/)
    {
      $word = $1;
      $pos = $2;
      $sense = $3;
      return () if($sense !~ /[0-9]+/ || $pos !~ /^[nvar]$/);
      @senses = $wn->querySense($word."\#".$pos);
      foreach $key (@senses)
      {
        if($key =~ /\#$sense$/)
        {
          return ($key);
        }
      }
      return ();
    }
    elsif($word =~ /^([^\#]+)\#([^\#]+)$/)
    {
      $word = $1;
      $pos = $2;
      return () if($pos !~ /[nvar]/);
    }
    else
    {
      return ();
    }
    }
    else
    {
    $pos = "nvar";
    }
    
    # Get the senses corresponding to the raw form of the word.
    @senses = ();
    foreach $key ("n", "v", "a", "r")
    {
    if($pos =~ /$key/)
    {
      push(@senses, $wn->querySense($word."\#".$key));
    }
    }
    #print "@senses\n";
    return @senses;
}


# Get the sense of a topic word based on context words.
# Gets the sense based on the words that are not stopwords
sub getSenseFromContext #word, @context
{
    my ($word, $context_ref) = @_;
    my @c = @{$context_ref};
    
    @c = removeStopWords(\@c);
    my $size = @c;
    my $sense = "";
    print "Getting sense from context... $size\n";
    if ($size > 0){
        my $result = $wsd->disambiguate (target => $word,
                              context => [@c]);
        my $max = 0;
        
        foreach my $key (keys %$result) {
           my $r = $result->{$key};
           if ($r > $max) 
           { 
                $max = $r; 
                $sense = $key;
                #print "sense: $sense\n";
            }
        }
    }
    else
    {
        my $hashRef = {};             # Creates a reference to an empty hash.
        $hashRef->{text} = [];        # Value is an empty array ref.
        $hashRef->{words} = [];       # Value is an empty array ref.
        $hashRef->{wordobjects} = []; # Value is an empty array ref.
        $hashRef->{head} = -1;        # Index into the text array (initialized to -1)
        $hashRef->{target} = -1;      # Index into the words & wordobjects arrays (initialized to -1)
        $hashRef->{lexelt} = "";      # Lexical element (terminology from Senseval2)
        $hashRef->{id} = "";          # Some ID assigned to this instance
        $hashRef->{answer} = "";      # Answer key (only required for evaluation)
        $hashRef->{targetpos} = "";   # Part-of-speech of the target word (if known).
        
        my $wordobj = WordNet::SenseRelate::Word->new($word);
        push(@{$hashRef->{wordobjects}}, $wordobj);
        push(@{$hashRef->{words}}, $word);
        $hashRef->{target} = 0;        # Index of "bank"
        $hashRef->{id} = "Instance1";  # ID can be any string.
         ($sense, $error) = $tool->disambiguate($hashRef);
         
    }
    print "Done sense from context: $sense\n";
    return $sense;
}

sub removeStopWords #@context
{
    my $ref = @_[0];
    my @c = @{$ref};
    my @removed = ();
    
    for my $e (@c)
    {   
        my @elements = split(/#/, $e);
        if (not isStopWord($elements[0]))
        {
            push(@removed, $elements[0]);
        }
    }
    
    return @removed;
}

sub removeInvalidWord #@context
{
    my @context = @_;
    my @new = ();
    
    foreach my $c (@context)
    {
        my @elements = split(/#/, $c);
        my $size = @elements;
        #if (($size > 2) and not(isStopWord($elements[0])))
        if ($size > 2)
        { 
            push(@new, $c); 
        }
    }
    return @new;
}


# Wordnet vector relatedness
sub getRelatedness
{
    #print "Getting similarity...\n";
    my ($word1, $word2) = @_;
    #print "$word1 $word2\n";
    my $r = $vector->getRelatedness($word1, $word2);
    ($error, $errorString) = $vector->getError();
    die "$errorString\n" if($error);
    #print "$r\n"
    return $r;
}

=pod
sub getRelatedness
{
    my $querydata = WordNet::QueryData->new;
    my $vector = WordNet::Similarity::vector->new($wn);
    my %options = (wordnet => $querydata,
                      measure => 'WordNet::Similarity::vector');
    
    my $wsd = WordNet::SenseRelate::WordToSet->new (%options);
    
    my $result = $wsd->disambiguate (target => 'cake',
                          context => ["eat#v#1"]);
    
        # get the sense with the max value
    my $max = 0;
    my $sense = "";
    
    foreach my $key (keys %$result) 
    {
       my $r = $result->{$key};
       if ($r > $max) 
       { 
            $max = $r; 
            $sense = $key;
        }
    }
    print "$sense $max\n";
    
    my $r = $vector->getRelatedness("cake#n#1", "eat#v#1");
    ($error, $errorString) = $vector->getError();
    die "$errorString\n" if($error);
    print "$r\n"
}
=cut

sub readFile
{
    my ($filename, $base_path) = @_;

    my $path = $base_path . $filename . ".txt";

    open INFILE, $path or die $!;
    my @lines = <INFILE>;
    close INFILE;
    return @lines;
}

sub readTopics
{
    my $sense = @_[0];
    my @topics = ();    
    my @lines = readFile($sense, $topic_path);

    foreach my $line (@lines)
    {
        chomp($line);
        if (length($line) > 1) {
            push (@topics, $line);
        }
    }
    #print "topics: @topics\n";
    return @topics;
}

sub readContexts
{
    my $phrase_number = @_[0];
    my @contexts = ();

    my @lines = readFile($phrase_number, $path);
     
     foreach my $line (@lines)
    {
        # based on the sense of the context pick literal/figurative dictionary of topics
    }
    
}

sub formatContext
{
    my $ref = @_[0];
    my @c = @{$ref};
    my @array = ();
    foreach my $e (@c)
    {
        my @elements = split(/#/, $e);
        push(@array, lc($elements[0]));
    }
    
    return @array;
}

sub isStopWord
{
    my $word = $_[0];
    return exists $stopWords{lc($word)};
}

sub readStopWords
{
    open STOP, "/Users/edaena/Documents/SPRING2013/naed/data/stopwords.english" or die $!;
    my @lines = <STOP>;
    
    for my $line (@lines)
    {
        chomp($line);
        $stopWords{lc($line)} = 1;
    }
    close STOP;
}

