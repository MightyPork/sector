<?php

# Class for easier XML generation
class SimpleDocument extends DOMDocument{
	private $rootNode = null;
	public function __construct($rootName='xml', $attribs = array()){
		parent::__construct('1.0', 'UTF-8');
		$rootElement = $this->createElement($rootName, $attribs);

		$this->rootNode = $this->appendChild($rootElement);
	}

	public function createAttribute($name, $value){
		$a = parent::createAttribute($name);
		$a->value = $value;
		return $a;
	}

	public function createElement($name='element', $attribs = null, $inner=''){
		$elem = parent::createElement($name,$inner);

		if($attribs != null){
			foreach($attribs as $key => $value){
				$a = $this->createAttribute($key, $value);
				$elem->appendChild($a);
			}
		}
		
		return $elem;
	}

	public function appendChildToRoot(DOMElement $element){
		return $this->rootNode->appendChild($element);
	}
}
