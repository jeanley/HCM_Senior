select	a.numemp,
		a.numcad,
		a.nomfun,
		a.datadm,
		b.titcar,
		a.conrho,
		a.sitafa,
		c.dessit
from	r034fun a
		inner join r024car b on b.estcar = a.estcar and b.codcar = a.codcar
		left join r010sit c on c.codsit = a.sitafa
where	a.numemp < 5
and		a.sitafa not in (7,18) 
and		a.conrho = 2
and		a.numcad not in (select distinct numcad from r070acc where datapu between '2025-09-25' and '2025-10-24') 
and		datadm < getdate()


--select * from r070acc where numcad = 30005055

--select distinct usomar from r070acc where usomar <> 2
