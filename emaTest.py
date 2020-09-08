from __future__ import (absolute_import, print_function, division,
                        unicode_literals)
import matplotlib.pyplot as plt
import networkx as nx
import random
from ema_workbench import (Model, RealParameter, MultiprocessingEvaluator, CategoricalParameter,
                           IntegerParameter, ScalarOutcome, ArrayOutcome, Constant, ema_logging,
                           perform_experiments)
from ema_workbench.em_framework.evaluators import MC

from ema_workbench.analysis import feature_scoring
from ema_workbench.analysis import prim

from ema_workbench.analysis import scenario_discovery_util as sdutil

import pandas as pd
import seaborn as sns
import numpy as np


def update(g,r):
    count = 0
    elms = list(g.nodes.keys())
    while (count <= r):
        e = elms[random.randint(0,len(elms)-1)]
        elms.remove(e)
        g.remove_node(e)
        count += 1
    return g

def simulate_ER1(x):
    n = int(x[0])
    p = x[1]
    replications = int(x[2])
    f = x[3]
    outcomes = []
    Sf=0
    for i in range(replications):
        random.seed()
        removeCnt = int(n*f)
        er = update(nx.erdos_renyi_graph(n, p),removeCnt)
        largest_cc = max(nx.connected_components(er), key=len)
        Sf=len(largest_cc)
        outcomes.append(Sf/n)
    cc = sum(outcomes)/len(outcomes)
    return cc

def simulate_ER(n=10, p=0.5, replications=10, f=0.6):
    outcomes = []
    Sf=0
    for i in range(replications):
        random.seed()
        removeCnt = int(n*f)
        er = update(nx.erdos_renyi_graph(n, p),removeCnt)
        largest_cc = max(nx.connected_components(er), key=len)
        Sf=len(largest_cc)
        outcomes.append(Sf/n)
    cc = sum(outcomes)/len(outcomes)
    return {'cc': cc}


def performExperiments():

    ema_logging.LOG_FORMAT = '[%(name)s/%(levelname)s/%(processName)s] %(message)s'
    ema_logging.log_to_stderr(ema_logging.INFO)

    model = Model('SimulateER', function=simulate_ER)  # instantiate the model
    # specify uncertainties
    model.uncertainties = [RealParameter("p", 0.1, 1.0)]
    model.uncertainties = [RealParameter("f", 0.1, 0.9)]

    model.levers = [IntegerParameter("n", 10, 100)]

    # specify outcomes
    model.outcomes = [ScalarOutcome('cc')]

    model.constants = [Constant('replications', 10)]

    n_scenarios = 10
    n_policies = 10

    res = perform_experiments(model, n_scenarios, n_policies)

    experiments, outcomes = res
    data= experiments[['n', 'p', 'f']]
    data.to_csv('out.csv',index=False)
    return data


def tab_to_DF(files):
    frames= []
    for f in files:
        cdf =pd.read_fwf(f)
        fname = f+".csv"
        cdf.to_csv(fname)
        df = pd.read_csv(fname)
        df1 = df.iloc[:,1:28]
        frames.append(df1)
    result = pd.concat(frames)
    return result

#if __name__ == '__main__':





