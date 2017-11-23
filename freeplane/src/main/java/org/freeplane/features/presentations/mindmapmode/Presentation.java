package org.freeplane.features.presentations.mindmapmode;

public class Presentation implements NamedElement<Presentation>{
	private String name;
	public final NamedElementCollection<Slide> slides;
	private final NamedElementFactory<Slide> slideFactory;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Presentation(String name, NamedElementFactory<Slide> slideFactory) {
		super();
		this.slideFactory = slideFactory;
		this.name = name;
		slides = new NamedElementCollection<>(slideFactory);
	}
	
	public Presentation create(String name) {
		return new Presentation(name, slideFactory);
	}
	
	public Presentation saveAs(String name) {
		final Presentation presentation = new Presentation(name, slideFactory);
		for(Slide slide : slides)
			presentation.slides.add(slide.saveAs(slide.getName()));
		presentation.slides.selectCurrentElement(slides.getCurrentElementIndex());
		return presentation;
	}
}
