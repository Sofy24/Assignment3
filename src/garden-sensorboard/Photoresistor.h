#ifndef __PHOTORES__
#define __PHOTORES__

class Photoresistor{
    public:
    Photoresistor(int pin);
    int getLuminosity();

    private:
    int pin;
};

#endif
