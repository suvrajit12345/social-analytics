import { us } from './us';
var stateName;
export var searchState = function(x)
{
	var u = us;
	if (x.indexOf(',') > -1) 
	{ 
		let y=x.split(',');
		for(let z=0;z<y.length;z++) s(y[z].trim(),u)
	} 
	else s(x,u)
	if(stateName) return stateName;
	else return null;
}
function s(d,u){
	for(let i=0;i<u.length;i++)
	{
		for(let j=0;j<u[i].city.length;j++)
		{
			if(d.toUpperCase()==u[i].city[j].toUpperCase()) stateName=u[i].name;
		}
		if(d.toUpperCase()==u[i].name.toUpperCase()) stateName=u[i].name;
		else if(d.toUpperCase()==u[i].Abbrev.toUpperCase()) stateName=u[i].name;
		else if(d.toUpperCase()==u[i].Code.toUpperCase()) stateName=u[i].name;
	}
}
