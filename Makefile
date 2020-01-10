#create empty directory
define mkdir
	mkdir -p $(1);
	touch $(1)/tmp;
	rm -r $(1)/*;
endef

#compile and document
all: compile document

#compile to build directory (java 7 minimum)
compile:
	$(call mkdir, tmp)

	javac -g -deprecation -Xlint -d tmp -cp . src/DLibX/*.java src/DLibX/util/*.java src/DLibX/legacy/*.java src/Test/*.java src/Main/*.java

	$(call mkdir, build)

	mv tmp/* build

	bash src/increment_version.sh
	
	rm -r tmp

#run tests
test: compile
	java -verbose:gc -cp .:build Test.Test

#generate javadoc
document:
	$(call mkdir, doc)
	javadoc -quiet -public -d doc -linksource src/DLibX/*.java src/DLibX/util/*.java src/DLibX/legacy/*.java

#package for release
package: all
	$(call mkdir, tmp)

	bash src/increment_version.sh package

	jar cfm tmp/DLibX.jar src/DLibX.mf -C build DLibX build/Main build/Main/VERSION

	cd doc && zip -rq9 ../tmp/DLibX-Documentation.zip * && cd ..
	cd src && zip -rq9 ../tmp/DLibX-Sources.zip DLibX/*.java DLibX/util/*.java DLibX/legacy/*.java && cd ..

	cp LICENSE tmp/LICENSE
	cp README.md tmp/README.md

	cd tmp && zip -rq9 DLibX.zip * && cd ..

	mv tmp/DLibX.zip build/DLibX-current.zip
	mv tmp/DLibX.jar build/DLibX.jar
	mv tmp/DLibX-Documentation.zip build/DLibX-Documentation.zip
	cp VERSION build/VERSION

	rm -r tmp

#clean up
clean:
	rm -r build doc release || true