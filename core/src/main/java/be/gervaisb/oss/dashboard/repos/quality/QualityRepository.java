package be.gervaisb.oss.dashboard.repos.quality;

import be.gervaisb.oss.dashboard.Reference;

public interface QualityRepository {

    Quality getQuality(Reference reference);

}
