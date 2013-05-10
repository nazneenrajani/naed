
val input = io.Source.fromFile("data/unique-phrases/1-train.txt").getLines
val bigramMap = input.flatMap(x => Tokenize(x.toLowerCase)).sliding(2).flatMap{case List(p,q) => List(p+" "+q)}.toList.zipWithIndex.toMap
//.groupBy(x=>x).mapValues(x=>x.length.toDouble).toMap
for((k,v)<- bigramMap)
println(k +"->"+v)
//bigramMap.foreach(println)
val train = io.Source.fromFile("data/unique-phrases/1-train.txt").getLines.toList
var trainVector = Array.fill(train.size,bigramMap.size)(0)
for(i <- 0 to train.size-1){
	val bigram = Tokenize(train(i).toLowerCase).sliding(2).flatMap{case List(p,q) => List(p+" "+q)}.toList
	println(bigram)
	var j = 0
	while(j < bigram.size){
		if(bigramMap.contains(bigram(j))){
			val value = bigramMap.get(bigram(j))
			trainVector(i)(value)++
			j = j+1
		}
		else
			j= j+1
	}	
	 
}
def Tokenize(text: String): IndexedSeq[String]={
      val starts = """(?:[#@])|\b(?:http)"""
      text
      .replaceAll("""([\?!()\";\|\[\].,':])""", " $1 ")
      .trim
      .split("\\s+")
      .filterNot(x => x.matches("""[:'.'"!,?/()\d]+"""))
      .toIndexedSeq
      .filterNot(x => x.contains(starts))
  }