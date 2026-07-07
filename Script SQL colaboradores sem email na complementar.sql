select	a.numemp,
		a.tipcol,
		a.numcad,
		a.nomfun,
		a.datadm,
		car.titcar,
		a.sitafa,
		a.numcpf,
		b.emacom,
		c.codusu,
		d.nomusu,
		e.nomfun,
		e.sitafa,
		f.codusu, 
		e.datadm
from	r034fun (nolock) a 
        left join r034cpl (nolock) b on b.numemp = a.numemp and b.tipcol = a.tipcol and b.numcad = a.numcad
		left join r034usu (nolock) c on c.numemp = a.numemp and c.tipcol = a.tipcol and c.numcad = a.numcad
		left join r999usu (nolock) d on d.codusu = c.codusu
		left join r024car (nolock) car on car.estcar = a.estcar and car.codcar = a.codcar
		inner join r034fun (nolock) e on e.numcpf = a.numcpf and e.sitafa not in (7,18)
		left join r034usu (nolock) f on f.numemp = e.numemp and f.tipcol = e.tipcol and f.numcad = e.numcad 
where	a.tipcol = 1
and		a.sitafa not in (7,18)
and		(b.emacom is null or b.emacom = '' or b.emacom not like '%@maristabrasil.org')
and		a.numemp < 5
