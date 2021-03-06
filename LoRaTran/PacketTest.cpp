#include<iostream>
using namespace std;

#include"postman_packet.h"
#include"CarMapSystem.h"

//#define NO_CAR_MODE
Coor init,xMax,yMax;

#define MAP_WIDTH 140
#define MAP_HEIGHT 41
#define ROAD_WIDTH 5


float** adj;

void initCGMS(){
	CarGpsMapSystem* cgms = CarGpsMapSystem::getInstance(MAP_WIDTH,MAP_HEIGHT,init,xMax,yMax);

	Stronghold sArr[5][3];
    int value = 0;
    int xScale = (MAP_WIDTH - ROAD_WIDTH)/4;
    int yScale = (MAP_HEIGHT - ROAD_WIDTH)/2;
    for(int i = 0 ; i < 5;i++){
        for(int j = 0; j < 3; j++){
            value++;
            sArr[i][j].value = value;
            sArr[i][j].x = i * xScale + ROAD_WIDTH/2;
            sArr[i][j].y = j * yScale + ROAD_WIDTH/2;
            cgms->addStronghold(sArr[i][j]);
        }
    }

    cgms->fillNodeStronghold();
    //cgms->printNodeStronghold();

    int n = cgms->getStrongholdNum();

    adj = new float*[n + 1];
    for (int i = 0; i <= n; i++) {
        adj[i] = new float[n + 1];
    }

    for (int i = 1; i <= n; i++) {
        for (int j = 1; j <= n; j++) {
            adj[i][j] = MAX_FLOAT;
        }
    }
    for(int i = 1; i <= 3 ;i+=2){
        for(int j = 0; j <= 9; j+=3){
            adj[i+j][i+j+3] = yScale;
            adj[i+j+3][i+j] = yScale;
        }
    }
    for(int i = 1; i <= 13;i+=6){
        for(int j = 0; j < 2; j++){
            adj[i+j][i+j+1] = xScale;
            adj[i+j+1][i+j] = xScale;
        }
    }
}

int main(){

    init.x = 121.370854;
    init.y = 24.943516;
    xMax.x = 121.373225;
    xMax.y = 24.944878;
    yMax.x = 121.370426;
    yMax.y = 24.944117;

    vec_CMnode traceVec;

    CarGpsMapSystem* cgms = CarGpsMapSystem::getInstance(MAP_WIDTH,MAP_HEIGHT,init,xMax,yMax);
    cgms->setRectangleWall( 0 + ROAD_WIDTH, 0 + ROAD_WIDTH,MAP_WIDTH/2 - ROAD_WIDTH ,MAP_HEIGHT - ROAD_WIDTH);
    cgms->setRectangleWall(MAP_WIDTH/2 + ROAD_WIDTH, 0 + ROAD_WIDTH,MAP_WIDTH - ROAD_WIDTH,MAP_HEIGHT - ROAD_WIDTH);

    initCGMS();


    Coor ss1,ee1;
    
    ss1.x =  121.371;
    ss1.y =  24.9436;
    //ee1.x = 121.371;
    //ee1.y = 24.9439;
    ee1.x = 121.373166;
    ee1.y = 24.944927;  
/*
    ss1.x = init.x;
    ss1.y = init.y;
    ee1.x = yMax.x;
    ee1.y = yMax.y;
    */
    Coor ss2 = cgms->gpsToCoordinate(ss1);
    Coor ee2 = cgms->gpsToCoordinate(ee1);

/*
    ss2.x = 1399;
    ss2.y = 0;
    ee2.x = 0;
    ee2.y = 399;
*/

    Coor ss3 = cgms->coordinateToGps(ss2);
    Coor ee3 = cgms->coordinateToGps(ee2);


    printf("ss1.x = %lf,%lf\n",ss1.y,ss1.x);
    printf("ee1.x = %lf,%lf\n",ee1.y,ee1.x);
    printf("ss2.x = %lf,%lf\n",ss2.y,ss2.x);
    printf("ee2.x = %lf,%lf\n",ee2.y,ee2.x);
    printf("ss3.x = %lf,%lf\n",ss3.y,ss3.x);
    printf("ee3.x = %lf,%lf\n",ee3.y,ee3.x);


    Coor ee;
    ee.x =  121.371951;
	ee.y = 24.944224;
    Coor	mapNode = cgms->gpsToCoordinate(ee);
	if(!cgms->isInsideMap(ee.x,ee.y)){
			printf("goToLocation : cgms detect destmation not in map region, lon:%lf, lat:%lf, mapLon:%lf ,mapLat:%lf\n",ee.x,ee.y,mapNode.x,mapNode.y);
			//return 0;
	}
    else{
        printf("goToLocation :  lon:%lf, lat:%lf, mapLon:%lf ,mapLat:%lf\n",ee.x,ee.y,mapNode.x,mapNode.y);
    }

    int x,y;
    //cin >> x >> y;
 
    mapNode = cgms->gpsToCoordinate(ee);
	if(!cgms->isInsideMap(ee.x,ee.y)){
			printf("goToLocation : cgms detect destnation not in map region\n");
			printf("goToLocation :lon:%lf, lat:%lf, mapLon:%lf ,mapLat:%lf\n",ee.x,ee.y,mapNode.x,mapNode.y);

			cgms->fixOutNode(mapNode);

			ee = cgms->coordinateToGps(mapNode);
			mapNode = cgms->gpsToCoordinate(ee);

			printf("fix to : lon:%lf, lat:%lf,mapLon:%lf,mapLat:%lf\n",ee.x,ee.y,mapNode.x,mapNode.y);

			//return GPS_NOT_IN_CARMAP;
	}


    Coor src,dest;

    src.x = 121.371511;
    src.y = 24.943946;

    dest.x = 121.372150;
    dest.y = 24.945091;

    cgms->findPath(traceVec,src,dest,adj);


    for(vec_CMnode::iterator it = traceVec.begin(); it != traceVec.end();it++){
        cout << "x:" << (*it)->GetCor_x()
                << ",y:"<< (*it)->GetCor_y()
                << ",dir:"<< (*it)->getDir()
                <<endl;
    }

    
    ::cout << "good";
    
    
    cin.get();
    cin.get();
}