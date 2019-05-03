#define tolong(A) ((A)>=0.0?floor((A)):ceil((A)))
double gregorian2julianDN(int year, int month, int day, int hour, int minute, int second, int adbc)
{
     int iy = tolong(year);
     int signal = (adbc == 0 ? -1 : 1);
     double jyear= signal*iy, ja, jmonth, julian;
     if (iy < 1) {return -1;}
     if (jyear < 0) ++jyear;
     if (month > 2) {jmonth = month + 1;} else { --jyear; jmonth = month + 13;}
     julian = tolong(floor(365.25 * jyear) + floor(30.6001 * jmonth) + day + 1720995);
     if (day+31*(month+12*signal*iy) >= (14 + 31 * (10 + 12 * 1582))) {
         ja = tolong(0.01 * jyear);
         julian += 2 - ja + tolong(0.25 * ja);
     }
     julian += (hour-12)/24.0 + minute/1440.0 + second/86400.0;

     return julian;
}

int julianDN2gregorian(double jdn, int gregorian[])
{
    int hour, day, month, year, adbc, minute, second;
    double ja, jb, jc, jd, je, jalpha;
    double remain = jdn - floor(jdn);
    long julian = tolong(jdn);
    long jr = tolong(remain*86400);
    if (julian > 2299160) {
        jalpha = tolong(((julian - 1867216) - 0.25)/36524.25);
        ja = julian + 1 + jalpha - tolong(0.25 * jalpha);
    } else {
        ja = julian;
    }
    jb = ja + 1524;
    jc = tolong(6680 + ((jb - 2439870) - 122.1)/365.25);
    jd = tolong(365 * jc + 0.25 * jc);
    je = tolong((jb - jd)/30.6001);

    hour = jr/3600 + 12;
    if (hour >= 24){
        hour %=24;
        day = jb - jd - tolong(30.6001 * je) + 1;
    }else{
        day = jb - jd - tolong(30.6001 * je);
    }
    month = je - 1;
    if (month > 12) month -= 12;
    year = jc - 4715;
    if (month > 2) --(year);
    adbc=1;
    if (year <= 0) { year = 1 - year; adbc = 0;}
    minute = jr%3600/60;
    second = jr%3600%60;
    
    gregorian[0]=year;
    gregorian[1]=month;
    gregorian[2]=day;
    gregorian[3]=hour;
    gregorian[4]=minute;
    gregorian[5]=second;
    gregorian[6]=adbc;

    return 0;
}
