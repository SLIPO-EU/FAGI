INSERT INTO fused_geometries (subject_A, subject_B, geom)
SELECT links.nodea, links.nodeb, CASE WHEN points_a > points_b 
					THEN a_g
					ELSE b_g
				      END AS geom
FROM links 
INNER JOIN (SELECT dataset_a_geometries.subject AS a_s, 
		   dataset_b_geometries.subject AS b_s,
		  dataset_a_geometries.geom AS a_g, 
		  dataset_b_geometries.geom AS b_g,
		  ST_NPoints(dataset_a_geometries.geom) AS points_a,
		  ST_NPoints(dataset_b_geometries.geom) AS points_b
		FROM dataset_a_geometries, dataset_b_geometries) AS geoms 
		ON(links.nodea = geoms.a_s AND links.nodeb = geoms.b_s)

-- Verify 1
SELECT na, nb, CASE WHEN points_a > points_b THEN ST_asText(a_g)
            ELSE ST_asText(b_g)
       END AS geom
FROM (SELECT links.nodea AS na, b.subject AS b_s, b.geom AS b_g, ST_NPoints(b.geom) AS points_b
FROM links INNER JOIN dataset_b_geometries AS b
ON (links.nodeb = b.subject)) AS geom_b
INNER JOIN
(SELECT a.subject AS a_s, links.nodeb AS nb, a.geom AS a_g, ST_NPoints(a.geom) AS points_a
FROM links INNER JOIN dataset_a_geometries AS a
ON (links.nodea = a.subject)) AS geom_a ON (geom_a.a_s = na AND geom_b.b_s = nb)

-- Verify 2
SELECT links.nodea, links.nodeb, CASE WHEN points_a > points_b THEN ST_asText(a_g)
            ELSE ST_asText(b_g)
       END AS geom
FROM links 
INNER JOIN (SELECT dataset_a_geometries.subject AS a_s, 
		   dataset_b_geometries.subject AS b_s,
		  dataset_a_geometries.geom AS a_g, 
		  dataset_b_geometries.geom AS b_g,
		  ST_NPoints(dataset_a_geometries.geom) AS points_a,
		  ST_NPoints(dataset_b_geometries.geom) AS points_b
		FROM dataset_a_geometries, dataset_b_geometries) AS geoms ON(links.nodea = geoms.a_s AND links.nodeb = geoms.b_s)
