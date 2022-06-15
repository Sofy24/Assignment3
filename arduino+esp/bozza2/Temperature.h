#ifndef __TEMPERATURE__
#define __TEMPERATURE__

class Temperature{
    public:
    Temperature(int pin);
    int getTemperature();

    private:
    int pin;
};

#endif
