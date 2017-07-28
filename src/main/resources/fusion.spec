<?xml version="1.0" encoding="UTF-8"?>
<SPECIFICATION>
	<INPUTFORMAT>NT</INPUTFORMAT>
	<OUTPUTFORMAT>NT</OUTPUTFORMAT>
	<LEFT>
		<ID>OSM</ID>
		<ENDPOINT></ENDPOINT>
		<FILE></FILE>
	</LEFT>
	<RIGHT>
		<ID>WIKIMAPIA</ID>
		<ENDPOINT></ENDPOINT>
		<FILE></FILE>
	</RIGHT>
	<TARGET>
		<ID>FUSED_OSM_WIKIMAPIA</ID>
		<FILE></FILE>
	</TARGET>
	<STRATEGY>
         <MATCH>
               <PropertyA name="Address"/>
               <PropertyB name="MyAddress" />
               <Rule>RuleA</Rule>
               <Validation>Keep Valid</Validation>
               <Priority>Default</Priority>
               <Action>Keep Left</Action>
         </MATCH>
	</STRATEGY>
</SPECIFICATION>
