# get data from file
# column 1 is labels (row names)
w <- getwd()
w
#setwd("/Users/cyprien/Desktop/testRgene")
setwd(".visulogRTempFiles")

#z <- paste(w,".visulogRTempFiles",sep="/")
x <- read.table("commitsPA.txt", header = FALSE)
colnames(x) <- c("auteurs", "nb")
attach(x)

## SETTINGS:
prob <- FALSE # set to TRUE if barplot(%) desired
tofile <- TRUE # set to FALSE to print in RStudio




pct <- 100*nb/sum(nb) # normalize to empirical probabilities (%)
# build colors from %
pcgood <- 10 # level qualifying what "good" means
cl <- (pct < pcgood)*2 # red
cl[cl == 0] <- 3   # green

# Do the plot
clab <- 1.4    # label size, 1 = default size
cnames <- 0.1  # bar names size, adapt to nb of bars
lgdbon <- paste("bon (>", pcgood,"%)")
setwd("../")
setwd(".graphs")

if (! tofile) if (prob) {
  barplot(pct, names.arg = auteurs, main="Commits par auteurs (%)",
        ylim = c(0, min(c(100, max(pct)+10))),
        xlab="auteurs", ylab="% de commits" , 
        cex.names = cnames, cex.lab=clab, col=cl, las = 2)
  legend("topleft", legend = c(lgdbon,"mauvais"), fill=3:2,
       bg = "antiquewhite")
  # visualize "good" level...
  abline(h = pcgood, lty=2, col=8)
} else {
  barplot(nb, names.arg = auteurs, main="Commits par auteurs",
          xlab="auteurs", ylab="nombre de commits", 
          cex.names = cnames, cex.lab=clab, las = 2)}

# output plot to a file in png; see ?pdf or ?png
if (tofile) {
  pdf(file = "CommitsPerAuthor.pdf", width=10, height=8) # 1920/1080 full HD png
  clab <- 1.4    # label size, 1 = default size
  cnames <- 0.7  # bar names size, adapt to nb of bars
  
  if (prob) {
    barplot(pct, names.arg = auteurs, main="Commits par auteurs (%)",
            ylim = c(0, min(c(100, max(pct)+10))),
            xlab="auteurs", ylab="% de commits" , 
            cex.names = cnames, cex.lab=clab, col=cl, las = 2)
    legend("topleft", legend = c(lgdbon,"mauvais"), fill=3:2,
           bg = "antiquewhite")
    # visualize "good" level...
    abline(h = pcgood, lty=2, col=8)
  } else {
    barplot(nb, names.arg = auteurs, main="Commits per author",
            xlab="", ylab="number of commits", 
            cex.names = cnames, cex.lab=clab, las = 2)}
  
  dev.off() # close file
}


