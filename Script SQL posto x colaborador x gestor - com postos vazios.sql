select	a.postra as postoColaborador,
		b.despos as nomePosto,
		a.numemp as empresaColaborador,
		a.codfil as filialColaborador,
		a.numcad as matriculaSenior,
		a.usu_matadp as matriculaLegado,
		a.nomfun as nomeColaborador,
		a.tipcol as tipoColaborador,
		a.codcar as codigoCargoColaborador,
		c.titcar as nomeCargo,
		a.codccu as centroCustoColaborador,
		d.nomccu as nomeCentroCusto,
		e.codsin as codSindicato,
		f.nomsin as nomeSindicato,
		g.nomloc as nomeLocal,
		h.dessit as situacaoColaborador,
		i.nomeGestor,
		car.varcar as permiteVariosCargos,
		car.codcar as codigoCargoPosto,
		j.perins as insalubridade,
		j.perper as periculosidade
from	r017pos (nolock) b 
		left join r034fun (nolock) a on b.estpos = a.estpos and b.postra = a.postra
		inner join r017car (nolock) car on car.estpos = b.estpos and car.postra = b.postra 
		                              and car.datini = (select max(datini) from r017car car where car.estpos = b.estpos and car.postra = b.postra)
		left join r024car (nolock) c on c.estcar = a.estcar and c.codcar = a.codcar
		left join r018ccu (nolock) d on d.numemp = a.numemp and d.codccu = a.codccu
        left join r038hsi (nolock) e on e.numemp = a.numemp and e.tipcol = a.tipcol and e.numcad = a.numcad
		                            and e.datalt = (select max(datalt) from r038hsi hsi where hsi.numemp = a.numemp and hsi.tipcol = a.tipcol and hsi.numcad = a.numcad)
		left join r014sin (nolock) f on f.codsin = e.codsin
        left join r016orn (nolock) g on g.taborg = a.taborg and g.numloc = a.numloc
        left join r010sit (nolock) h on h.codsit = a.sitafa
        left join usu_vhieCol i on i.codigoEmpresaColaborador = a.numemp and i.tipoColaborador = a.tipcol and i.matriculaColaborador = a.numcad and i.postoColaborador = a.postra
		left join r017adi j on b.estpos = j.estpos and b.postra = j.postra
		                   and j.datalt = (select max(datalt) from r017adi adi where adi.estpos = b.estpos and adi.postra = b.postra)
where 	1 = 1
and		b.estpos = 2
and   	a.numemp < 5
and   	a.sitafa not in (7,18)
--and		a.codfil in (2001,2026,2027,2030,2033,2049,2050,2051,2052,2053,2055,2057)

UNION
select	distinct a.postra as postoColaborador,
		b.despos as nomePosto,
		a.numemp as empresaColaborador,
		a.codfil as filialColaborador,
		null as matriculaSenior,
		null as matriculaLegado,
		null as nomeColaborador,
		null as tipoColaborador,
		null as codigoCargoColaborador,
		c.titcar as nomeCargo,
		a.codccu as centroCustoColaborador,
		d.nomccu as nomeCentroCusto,
		a.codsin as codSindicato,
		f.nomsin as nomeSindicato,
		g.nomloc as nomeLocal,
		null as situacaoColaborador,
		null as nomeGestor,
		a.varcar as permiteVariosCargos,
		a.codcar as codigoCargoPosto,
		j.perins as insalubridade,
		j.perper as periculosidade
from	r017pos (nolock) b 
		inner join r017car (nolock) a on a.estpos = b.estpos and a.postra = b.postra 
		                              and a.datini = (select max(datini) from r017car car where car.estpos = b.estpos and car.postra = b.postra)
		left join r024car (nolock) c on c.estcar = a.estcar and c.codcar = a.codcar
		left join r018ccu (nolock) d on d.numemp = a.numemp and d.codccu = a.codccu
        --left join r038hsi (nolock) e on e.codsin = a.codsin
		left join r014sin (nolock) f on f.codsin = a.codsin
        left join r016orn (nolock) g on g.taborg = a.taborg and g.numloc = a.numloc
        --left join usu_vhieCol i on i.codigoEmpresaColaborador = a.numemp and i.tipoColaborador = a.tipcol and i.matriculaColaborador = a.numcad and i.postoColaborador = a.postra
		left join r017adi j on b.estpos = j.estpos and b.postra = j.postra
		                   and j.datalt = (select max(datalt) from r017adi adi where adi.estpos = b.estpos and adi.postra = b.postra)
where 	1 = 1
and		b.estpos = 2
and		b.postra not in (select postra from r034fun where estpos = 2 and sitafa not in (7,18))

